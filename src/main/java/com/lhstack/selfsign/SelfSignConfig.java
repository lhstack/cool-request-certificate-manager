package com.lhstack.selfsign;

import java.util.Set;

public class SelfSignConfig {

    private CA ca;

    private Certificate certificate;

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

        private String dn;

        private Set<String> hosts;

        private Integer validityYear;

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

        private String dn;

        private Integer validityYear;

        private SelfSignAlgorithm algorithm;

        public SelfSignAlgorithm getAlgorithm() {
            return algorithm;
        }

        public CA setAlgorithm(SelfSignAlgorithm algorithm) {
            this.algorithm = algorithm;
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
