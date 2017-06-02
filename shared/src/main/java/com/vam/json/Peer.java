package com.vam.json;

/**
 * Created by michaelmeyer on 5/13/17.
 */
public class Peer {

    private String ip;
    private int port;
    private String continent;
    private String country;
    private String market;
    private boolean isSuper;

    public Peer() {}

    public Peer(String ip, int port, String continent, String country, String market, boolean isSuper) {
        this.ip = ip;
        this.port = port;
        this.continent = continent;
        this.country = country;
        this.market = market;
        this.isSuper = isSuper;
    }

    @Override
    public String toString() {
        return "Peer{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                ", continent='" + continent + '\'' +
                ", country='" + country + '\'' +
                ", market='" + market + '\'' +
                ", isSuper=" + isSuper +
                '}';
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
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
