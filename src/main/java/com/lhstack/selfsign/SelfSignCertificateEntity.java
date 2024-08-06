package com.lhstack.selfsign;

import java.security.PrivateKey;
import java.security.cert.Certificate;

public class SelfSignCertificateEntity {

    private final Certificate ca;

    private final PrivateKey caKey;

    private final Certificate certificate;

    private final PrivateKey certificateKey;

    public SelfSignCertificateEntity(Certificate ca, PrivateKey caKey, Certificate certificate, PrivateKey certificateKey) {
        this.ca = ca;
        this.caKey = caKey;
        this.certificate = certificate;
        this.certificateKey = certificateKey;
    }

    public Certificate getCa() {
        return ca;
    }

    public PrivateKey getCaKey() {
        return caKey;
    }

    public Certificate getCertificate() {
        return certificate;
    }

    public PrivateKey getCertificateKey() {
        return certificateKey;
    }
}
