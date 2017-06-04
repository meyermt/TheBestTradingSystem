package com.vam.json;

/**
 * Created by VictoriatheEast on 6/3/17.
 */
public class PeerPeerResponse {

    private boolean succeed;
    private TraderAction action;
    private double price;
    private String stock;

    public PeerPeerResponse(boolean succeed, TraderAction action, double price, String stock) {
        this.succeed = succeed;
        this.action = action;
        this.price = price;
        this.stock = stock;
    }

    public boolean isSucceed() {
        return succeed;
    }

    public TraderAction getAction() {
        return action;
    }

    public double getPrice() {
        return price;
    }

    public String getStock() {
        return stock;
    }
}
