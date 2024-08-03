package com.lhstack;

import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.collections.EnumerationUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;

import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

public class CertificateUtils {

    static {
        Security.addProvider(new BouncyCastleProvider());
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
                return pkcs8Export(keyStoreSupplier,passwordSupplier,items);
            }
            case "jks":
            default: {
                return jksExport(items);
            }
        }
    }

    private static byte[] pkcs8Export(Supplier<KeyStore> keyStoreSupplier, Supplier<char[]> passwordSupplier, List<Item> items) throws Exception {
        Item item = items.get(0);
        Key key = keyStoreSupplier.get().getKey(item.getName(), passwordSupplier.get());
        if(key instanceof PrivateKey){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] encoded = key.getEncoded();
            baos.write(encoded);
            return baos.toByteArray();
        }
        throw new RuntimeException("不支持导出pkcs8的证书类型");
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
        PemWriter pemWriter = new PemWriter(new OutputStreamWriter(baos));
        for (Item item : items) {
            pemWriter.writeObject(new PemObject("CERTIFICATE", item.getCertificate().getEncoded()));
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
