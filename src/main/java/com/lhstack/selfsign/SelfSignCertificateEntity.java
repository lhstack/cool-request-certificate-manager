package com.lhstack.selfsign;

import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

public class SelfSignCertificateEntity {

    private final X509Certificate ca;

    private final PrivateKey caKey;

    private final X509Certificate certificate;

    private final PrivateKey certificateKey;

    public SelfSignCertificateEntity(X509Certificate ca, PrivateKey caKey, X509Certificate certificate, PrivateKey certificateKey) {
        this.ca = ca;
        this.caKey = caKey;
        this.certificate = certificate;
        this.certificateKey = certificateKey;
    }

    public X509Certificate getCa() {
        return ca;
    }

    public PrivateKey getCaKey() {
        return caKey;
    }

    public X509Certificate getCertificate() {
        return certificate;
    }

    public PrivateKey getCertificateKey() {
        return certificateKey;
    }
}
