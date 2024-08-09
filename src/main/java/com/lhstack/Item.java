package com.lhstack;

import java.nio.charset.Charset;
import java.security.PublicKey;
import java.security.cert.Certificate;

public class Item {

    private Integer id;

    private String name;

    private String type;

    private String publicKeyContent;

    private String algorithm;

    private Certificate certificate;

    public Item setCertificate(Certificate certificate) {
        this.certificate = certificate;
        return this;
    }

    public Certificate getCertificate() {
        return certificate;
    }

    public String getPublicKeyContent() {
        return publicKeyContent;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public String getType() {
        return type;
    }

    public Item setType(String type) {
        this.type = type;
        return this;
    }

    public Integer getId() {
        return id;
    }

    public Item setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Item setName(String name) {
        this.name = name;
        return this;
    }


    public Item setPublicKey(PublicKey publicKey, Charset charset) {
        if(publicKey != null) {
            this.publicKeyContent = new String(publicKey.getEncoded(), charset);
            this.algorithm = publicKey.getAlgorithm();
        }
        return this;
    }
}
