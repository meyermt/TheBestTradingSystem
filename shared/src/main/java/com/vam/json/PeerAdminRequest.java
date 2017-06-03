package com.vam.json;

import java.util.List;

/**
 * Created by michaelmeyer on 5/31/17.
 */
public class PeerAdminRequest {

    private PeerAdminAction action;
    private String continent;
    private String country;
    private String market;
    private List<PeerAdapter> peers;
    private String sourceIP;
    private int sourcePort;

    public PeerAdminRequest() {}

    public PeerAdminRequest(PeerAdminAction action, String continent, String country, String market, List<PeerAdapter> peers, String sourceIP, int sourcePort) {
        this.action = action;
        this.continent = continent;
        this.country = country;
        this.market = market;
        this.peers = peers;
        this.sourceIP = sourceIP;
        this.sourcePort = sourcePort;
    }

    @Override
    public String toString() {
        return "PeerAdminRequest{" +
                "action=" + action +
                ", continent='" + continent + '\'' +
                ", country='" + country + '\'' +
                ", market='" + market + '\'' +
                ", peers=" + peers +
                ", sourceIP='" + sourceIP + '\'' +
                ", sourcePort=" + sourcePort +
                '}';
    }

    public PeerAdminAction getAction() {
        return action;
    }

    public String getMarket() {
        return market;
    }

    public String getSourceIP() {
        return sourceIP;
    }

    public int getSourcePort() {
        return sourcePort;
    }

    public String getContinent() {
        return continent;
    }

    public String getCountry() {
        return country;
    }

    public List<PeerAdapter> getPeers() {
        return peers;
    }
}
