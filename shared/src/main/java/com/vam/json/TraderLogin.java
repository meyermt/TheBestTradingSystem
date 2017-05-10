package com.vam.json;

/**
 * Created by michaelmeyer on 5/9/17.
 */
public class TraderLogin {

    private String sourceIP;
    private int sourcePort;

    public TraderLogin() {}

    public TraderLogin(String sourceIP, int sourcePort) {
        this.sourceIP = sourceIP;
        this.sourcePort = sourcePort;
    }

    public String getSourceIP() {
        return sourceIP;
    }

    public int getSourcePort() {
        return sourcePort;
    }
}
