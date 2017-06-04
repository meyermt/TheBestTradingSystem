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
    private List<PeerData> peers;
    private String sourceIP;
    private int sourceTraderPort;
    private int sourcePeerPort;

    public PeerAdminRequest() {}

    public PeerAdminRequest(PeerAdminAction action, String continent, String country, String market, List<PeerData> peers, String sourceIP, int sourceTraderPort, int sourcePeerPort) {
        this.action = action;
        this.continent = continent;
        this.country = country;
        this.market = market;
        this.peers = peers;
        this.sourceIP = sourceIP;
        this.sourceTraderPort = sourceTraderPort;
        this.sourcePeerPort = sourcePeerPort;
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
                ", sourceTraderPort=" + sourceTraderPort +
                ", sourcePeerPort=" + sourcePeerPort +
                '}';
    }

    public PeerAdminAction getAction() {
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

    public List<PeerData> getPeers() {
        return peers;
    }

    public String getSourceIP() {
        return sourceIP;
    }

    public int getSourceTraderPort() {
        return sourceTraderPort;
    }

    public int getSourcePeerPort() {
        return sourcePeerPort;
    }
}
