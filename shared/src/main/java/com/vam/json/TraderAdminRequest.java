package com.vam.json;

/**
 * Created by michaelmeyer on 5/9/17.
 */
public class TraderAdminRequest {

    private String sourceIP;
    private int sourcePort;
    private TraderAdminAction action;
    private String country;
    private String failedPeerMarket;

    public TraderAdminRequest(String sourceIP, int sourcePort, TraderAdminAction action, String country, String failedPeerMarket) {
        this.sourceIP = sourceIP;
        this.sourcePort = sourcePort;
        this.action = action;
        this.country = country;
        this.failedPeerMarket = failedPeerMarket;
    }

    public String getSourceIP() {
        return sourceIP;
    }

    public int getSourcePort() {
        return sourcePort;
    }

    public TraderAdminAction getAction() {
        return action;
    }

    public String getCountry() {
        return country;
    }

    public String getFailedPeerMarket() {
        return failedPeerMarket;
    }
}
