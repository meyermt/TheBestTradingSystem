package com.vam.json;

/**
 * Created by michaelmeyer on 5/13/17.
 */
public class PeerData {

    private String ip;
    private int peerPort;
    private int traderPort;
    private String continent;
    private String country;
    private String market;
    private boolean isSuper;

    public PeerData() {}

    public PeerData(String ip, int peerPort, int traderPort, String continent, String country, String market, boolean isSuper) {
        this.ip = ip;
        this.peerPort = peerPort;
        this.traderPort = traderPort;
        this.continent = continent;
        this.country = country;
        this.market = market;
        this.isSuper = isSuper;
    }

    @Override
    public String toString() {
        return "PeerData{" +
                "ip='" + ip + '\'' +
                ", peerPort=" + peerPort +
                ", traderPort=" + traderPort +
                ", continent='" + continent + '\'' +
                ", country='" + country + '\'' +
                ", market='" + market + '\'' +
                ", isSuper=" + isSuper +
                '}';
    }

    public int getPeerPort() {
        return peerPort;
    }

    public int getTraderPort() {
        return traderPort;
    }

    public String getIp() {
        return ip;
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

    public boolean isSuper() {
        return isSuper;
    }
}