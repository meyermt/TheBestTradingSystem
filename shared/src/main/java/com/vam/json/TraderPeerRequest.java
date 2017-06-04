package com.vam.json;

/**
 * Created by VictoriatheEast on 6/2/17.
 */
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

/**
 * Created by VictoriatheEast on 6/1/17.
 */


public class TraderPeerRequest {
    private String sourceIP;
    private int sourcePort;
    private TraderAction action;
    private Stock stock;
    private String continent;
    private String market;
    private int shares;
    private double price;

    public TraderPeerRequest(String sourceIP, int sourcePort, TraderAction action, Stock stock, String continent, String market, int shares, double price) {
        this.sourceIP = sourceIP;
        this.sourcePort = sourcePort;
        this.action = action;
        this.stock = stock;
        this.continent = continent;
        this.market = market;
        this.shares = shares;
        this.price = price;
    }

    @Override
    public String toString() {
        return "TraderPeerRequest{" +
                "sourceIP='" + sourceIP + '\'' +
                ", sourcePort=" + sourcePort +
                ", action=" + action +
                ", stock=" + stock +
                ", continent='" + continent + '\'' +
                ", market='" + market + '\'' +
                ", shares=" + shares +
                ", price=" + price +
                '}';
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

    public String getMarket() {
        return market;
    }

    public double getPrice() {
        return price;
    }

    public TraderAction getAction() {
        return action;
    }

    public Stock getStock() {
        return stock;
    }

    public int getShares() {
        return shares;
    }

}
