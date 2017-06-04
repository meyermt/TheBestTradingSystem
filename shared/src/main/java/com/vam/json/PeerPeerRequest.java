package com.vam.json;

/**
 * Created by VictoriatheEast on 6/3/17.
 */
public class PeerPeerRequest {

    private TraderAction action;
    private int shares;


    private String stockName;
    private double price;

    public PeerPeerRequest(TraderAction action, int shares, String stockName, double price){

        this.action = action;
        this.shares = shares;
        this.stockName = stockName;
        this.price = price;

    }


    public TraderAction getAction() {
        return action;
    }

    public int getShares() {
        return shares;
    }

    public String getStockName() {
        return stockName;
    }

    public double getPrice() {
        return price;
    }


}
