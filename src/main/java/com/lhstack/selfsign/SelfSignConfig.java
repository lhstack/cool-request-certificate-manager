package com.lhstack.selfsign;

import java.util.Collections;
import java.util.Set;

public class SelfSignConfig {

    /**
     * RSA,EC
     */
    private String algorithm;

    private CA ca;

    private Certificate certificate;

    public String getAlgorithm() {
        return algorithm;
    }

    public SelfSignConfig setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
        return this;
    }

    public Certificate getCertificate() {
        return certificate;
    }

    public SelfSignConfig setCertificate(Certificate certificate) {
        this.certificate = certificate;
        return this;
    }

    public CA getCa() {
        return ca;
    }

    public SelfSignConfig setCa(CA ca) {
        this.ca = ca;
        return this;
    }

    public static class Certificate{

        /**
         * CN=自签CA证书名称,L=北京市,ST=北京,C=中国,OU=IT部门
         */
        private String dn;

        /**
         * 信任的域名和ip
         */
        private Set<String> hosts = Collections.emptySet();

        /**
         * 证书有效期
         */
        private Integer validityYear;

        /**
         * 算法初始化长度
         */
        private Integer initializeSize;

        /**
         * 签名算法 SHA256withRSA SHA256withECDSA
         */
        private String signatureAlgorithm;


        public Integer getInitializeSize() {
            return initializeSize;
        }

        public Certificate setInitializeSize(Integer initializeSize) {
            this.initializeSize = initializeSize;
            return this;
        }

        public String getSignatureAlgorithm() {
            return signatureAlgorithm;
        }

        public Certificate setSignatureAlgorithm(String signatureAlgorithm) {
            this.signatureAlgorithm = signatureAlgorithm;
            return this;
        }

        public String getDn() {
            return dn;
        }

        public Certificate setDn(String dn) {
            this.dn = dn;
            return this;
        }

        public Set<String> getHosts() {
            return hosts;
        }

        public Certificate setHosts(Set<String> hosts) {
            this.hosts = hosts;
            return this;
        }

        public Integer getValidityYear() {
            return validityYear;
        }

        public Certificate setValidityYear(Integer validityYear) {
            this.validityYear = validityYear;
            return this;
        }
    }

    public static class CA{

        /**
         * CN=自签CA证书名称,L=北京市,ST=北京,C=中国,OU=IT部门
         */
        private String dn;

        /**
         * 证书有效期
         */
        private Integer validityYear;

        /**
         * 算法初始化长度
         */
        private Integer initializeSize;

        /**
         * 签名算法 SHA256withRSA SHA256withECDSA
         */
        private String signatureAlgorithm;


        public Integer getInitializeSize() {
            return initializeSize;
        }

        public CA setInitializeSize(Integer initializeSize) {
            this.initializeSize = initializeSize;
            return this;
        }

        public String getSignatureAlgorithm() {
            return signatureAlgorithm;
        }

        public CA setSignatureAlgorithm(String signatureAlgorithm) {
            this.signatureAlgorithm = signatureAlgorithm;
            return this;
        }

        public String getDn() {
            return dn;
        }

        public CA setDn(String dn) {
            this.dn = dn;
            return this;
        }

        public Integer getValidityYear() {
            return validityYear;
        }

        public CA setValidityYear(Integer validityYear) {
            this.validityYear = validityYear;
            return this;
        }
    }
}
