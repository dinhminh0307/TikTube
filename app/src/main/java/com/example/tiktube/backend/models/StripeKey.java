package com.example.tiktube.backend.models;

public class StripeKey {
    private String uid;
    private String privateKeyStripe;
    private String publicKeyStripe;

    public StripeKey() {}
    // Constructor
    public StripeKey(String uid, String privateKeyStripe, String publicKeyStripe) {
        this.uid = uid;
        this.privateKeyStripe = privateKeyStripe;
        this.publicKeyStripe = publicKeyStripe;
    }

    // Getters
    public String getUid() {
        return uid;
    }

    public String getPrivateKeyStripe() {
        return privateKeyStripe;
    }

    public String getPublicKeyStripe() {
        return publicKeyStripe;
    }

    public void setKey(StripeKey l) {
        this.uid = l.getUid();
        this.privateKeyStripe = l.getPrivateKeyStripe();
        this.publicKeyStripe = l.getPublicKeyStripe();
    }
}
