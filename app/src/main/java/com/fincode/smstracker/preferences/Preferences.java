package com.fincode.smstracker.preferences;

public class Preferences {
    private String phoneNumber;
    private String endpoint;
    private boolean sendEnabled;
    private boolean abortSms;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Preferences setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public Preferences setEndpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    public boolean isSendEnabled() {
        return sendEnabled;
    }

    public Preferences setSendEnabled(boolean sendEnabled) {
        this.sendEnabled = sendEnabled;
        return this;
    }

    public boolean isAbortSms() {
        return abortSms;
    }

    public Preferences setAbortSms(boolean abortSms) {
        this.abortSms = abortSms;
        return this;
    }
}