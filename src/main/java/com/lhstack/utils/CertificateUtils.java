package com.lhstack.utils;

import com.intellij.openapi.vfs.VirtualFile;
import com.lhstack.Item;
import org.apache.commons.collections.EnumerationUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.x509.X509V3CertificateGenerator;

import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

public class CertificateUtils {

    static {
        Provider provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
        if (provider == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }


    /**
     * 生成证书
     * SignatureAlgorithm: RSA=SHA256WithRSAEncryption EC=SHA256withECDSA
     *
     * @param algorithm 算法 RSA,EC
     * @param dn        DN CN=www.lhstack.com(域名或者ip), O=(组织或者企业), L=(市), ST=(省), C=(国家), OU=(部门)
     * @param period    时期
     * @return {@link Certificate}
     */
    public static Certificate gen(String algorithm, String dn, Duration period) throws Exception{
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        X509V3CertificateGenerator v3CertGen =  new X509V3CertificateGenerator();
        v3CertGen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
        v3CertGen.setIssuerDN(new X509Principal(dn));
        v3CertGen.setNotBefore(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24));
        v3CertGen.setNotAfter(new Date(System.currentTimeMillis() + period.toMillis()));
        v3CertGen.setSubjectDN(new X509Principal(dn));
        v3CertGen.setPublicKey(keyPair.getPublic());
        v3CertGen.setSignatureAlgorithm("SHA256withECDSA");
        //[ext] 多域名证书格式
        //subjectAltName = DNS:www.1111.com,DNS:www.2222.com,DNS:www.3333.com,IP:192.168.1.1
//        v3CertGen.addExtension("subjectAltName",true, new DERUTF8String(dn));
        return v3CertGen.generateX509Certificate(keyPair.getPrivate());
    }

    public static byte[] export(Supplier<KeyStore> keyStoreSupplier, Supplier<char[]> passwordSupplier, List<Item> items, String type) throws Exception {
        type = type.toLowerCase(Locale.ROOT);
        switch (type) {
            case "pem": {
                if (items.size() > 1) {
                    throw new RuntimeException("pem只支持单个证书导出");
                }
                return pemExport(items);
            }
            case "cer":
            case "crt": {
                if (items.size() > 1) {
                    throw new RuntimeException("crt只支持单个证书导出");
                }
                return crtExport(items);
            }
            case "pkcs8": {
                if (items.size() > 1) {
                    throw new RuntimeException("pkcs8只支持单个证书导出");
                }
                return pkcs8Export(keyStoreSupplier, passwordSupplier, items);
            }
            case "jks":
            default: {
                return jksExport(items);
            }
        }
    }

    private static byte[] pkcs8Export(Supplier<KeyStore> keyStoreSupplier, Supplier<char[]> passwordSupplier, List<Item> items) throws Exception {
        Item item = items.get(0);
        String password = JOptionPane.showInputDialog("请输入私钥密码");
        Key key = keyStoreSupplier.get().getKey(item.getName(), password.toCharArray());
        if (key instanceof PrivateKey) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] encoded = new PKCS8EncodedKeySpec(key.getEncoded()).getEncoded();
            baos.write(encoded);
            return baos.toByteArray();
        }
        throw new RuntimeException("不支持导出pkcs8的证书类型,请检查证书是否存在私钥");
    }

    private static byte[] crtExport(List<Item> items) throws Exception {
        Item item = items.get(0);
        return item.getCertificate().getEncoded();
    }

    private static byte[] jksExport(List<Item> items) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(null, null);
        for (Item item : items) {
            keyStore.setCertificateEntry(item.getName(), item.getCertificate());
        }
        String result = JOptionPane.showInputDialog("请输入需要导出的jks文件的密码,如果没有,则点击取消或者不输入直接确认", "changeit");
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        if (StringUtils.isNotBlank(result)) {
            keyStore.store(bo, result.toCharArray());
        } else {
            keyStore.store(bo, new char[0]);
        }
        return bo.toByteArray();
    }

    private static byte[] pemExport(List<Item> items) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JcaPEMWriter pemWriter = new JcaPEMWriter(new OutputStreamWriter(baos));
        for (Item item : items) {
            pemWriter.writeObject(item.getCertificate());
        }
        pemWriter.close();
        return baos.toByteArray();
    }

    public static Certificate load(VirtualFile virtualFile) throws Exception {
        String extension = virtualFile.getExtension().toLowerCase(Locale.ROOT);
        if (StringUtils.equalsAny(extension, "crt", "pem", "cer")) {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            try (InputStream inputStream = virtualFile.getInputStream()) {
                return cf.generateCertificate(inputStream);
            }
        }
        throw new RuntimeException("导入证书仅支持crt,pem,cer格式证书");
    }

    public static KeyStore convert(KeyStore keyStore, String extension) throws Exception {
        extension = extension.toLowerCase(Locale.ROOT);
        switch (extension) {
            case "pkcs12":
            case "p12":
            case "pfx": {
                KeyStore pfxKeyStore = KeyStore.getInstance("PKCS12");
                pfxKeyStore.load(null, null);
                List<String> alias = EnumerationUtils.toList(keyStore.aliases());
                for (String s : alias) {
                    pfxKeyStore.setCertificateEntry(s, keyStore.getCertificate(s));
                }
                return pfxKeyStore;
            }
            case "jks":
            default: {
                return keyStore;
            }
        }
    }
}
