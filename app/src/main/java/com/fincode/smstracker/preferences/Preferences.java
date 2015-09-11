package com.fincode.smstracker.preferences;

public class Preferences {
    private String phoneNumber;
    private String serverUrl;
    private boolean sendEnabled;
    private boolean abortSms;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Preferences setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public Preferences setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
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