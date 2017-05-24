package com.vam.json;

/**
 * Created by michaelmeyer on 5/23/17.
 */
public class PeerRequest {

    private String sourceIP;
    private int sourcePort;
    private PeerAction action;
    private String continent;
    private String country;
    private String market;

    public PeerRequest() {};

    public PeerRequest(String sourceIP, int sourcePort, PeerAction action, String continent, String country, String market) {
        this.sourceIP = sourceIP;
        this.sourcePort = sourcePort;
        this.action = action;
        this.continent = continent;
        this.country = country;
        this.market = market;
    }

    public String getSourceIP() {
        return sourceIP;
    }

    public int getSourcePort() {
        return sourcePort;
    }

    public PeerAction getAction() {
        return action;
    }

    public String getContinent() {
        return continent;
    }

    public String getCountry() {
        return country;
    }

    public String getMarket() {
        return market;
    }
}
