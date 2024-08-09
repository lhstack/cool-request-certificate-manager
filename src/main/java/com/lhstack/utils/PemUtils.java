package com.lhstack.utils;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;

public class PemUtils {

    static {
        Provider provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
        if (provider == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    public static final JcaPEMKeyConverter CONVERTER = new JcaPEMKeyConverter();

    public static PrivateKey readPrivateKey(String keyPem) throws Exception {
        PEMParser parser = new PEMParser(new StringReader(keyPem));
        Object o = parser.readObject();
        if(o instanceof PEMKeyPair){
            return CONVERTER.getPrivateKey(((PEMKeyPair) o).getPrivateKeyInfo());
        }
        return null;
    }


    /**
     * 到字符串
     *
     * @param object 对象 Certificate | PrivateKey
     * @return {@link String}
     * @throws Exception 例外
     */
    public static String toString(Object object) throws Exception {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        JcaPEMWriter pemWriter = new JcaPEMWriter(new OutputStreamWriter(bo));
        pemWriter.writeObject(object);
        pemWriter.close();
        bo.close();
        return bo.toString(StandardCharsets.UTF_8);
    }

    public static void pemWriter(Object object, String path) throws Exception {
        FileOutputStream fileOutputStream = new FileOutputStream(path);
        JcaPEMWriter pemWriter = new JcaPEMWriter(new OutputStreamWriter(fileOutputStream));
        pemWriter.writeObject(object);
        pemWriter.close();
        fileOutputStream.close();
    }
}
