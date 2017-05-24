package com.vam.json;

/**
 * Created by michaelmeyer on 5/9/17.
 */
public class TraderRequest {

    private String sourceIP;
    private int sourcePort;
    private TraderAction action;
    private String country;

    public TraderRequest() {}

    public TraderRequest(String sourceIP, int sourcePort, TraderAction action, String country) {
        this.sourceIP = sourceIP;
        this.sourcePort = sourcePort;
        this.action = action;
        this.country = country;
    }

    public String getSourceIP() {
        return sourceIP;
    }

    public int getSourcePort() {
        return sourcePort;
    }

    public TraderAction getAction() {
        return action;
    }

    public String getCountry() {
        return country;
    }
}
