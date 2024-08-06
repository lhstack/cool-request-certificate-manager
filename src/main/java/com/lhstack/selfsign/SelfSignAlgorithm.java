package com.lhstack.selfsign;

public enum SelfSignAlgorithm {
    RSA_1024("RSA","SHA256withRSA",1024),
    RSA_2048("RSA","SHA256withRSA",2048),
    RSA_4096("RSA","SHA256withRSA",4096),
    ECDSA_256("ECDSA","SHA256withECDSA",256),
    ECDSA_384("ECDSA","SHA256withECDSA",384),
    ;

    private final String algorithm;

    private final int size;
    private final String signatureAlgorithm;

    SelfSignAlgorithm(String algorithm,String signatureAlgorithm, int size) {
        this.algorithm = algorithm;
        this.signatureAlgorithm = signatureAlgorithm;
        this.size = size;
    }

    public String getSignatureAlgorithm() {
        return signatureAlgorithm;
    }

    public int getSize() {
        return size;
    }

    public String getAlgorithm() {
        return algorithm;
    }
}
