package com.vam.json;

/**
 * Created by VictoriatheEast on 6/1/17.
 */
public class TraderPeerResponse {

    private boolean succeed;
    private TraderAction action;
    private double price;
    private String stock;
    private int shares;
    private String targetIP;
    private int targetPort;

    public TraderPeerResponse(boolean succeed, TraderAction action, double price, String stock, int shares,String targetIP, int targetPort){
        this.succeed = succeed;
        this.action = action;
        this.price = price;
        this.stock = stock;
        this.shares = shares;
        this.targetIP = targetIP;
        this.targetPort = targetPort;
    }

    public boolean isSucceed() {
        return succeed;
    }

    public String getStockName() {
        return stock;
    }
    public boolean succeed() {
        return this.succeed;
    }

    public double getPrice() {
        return this.price;
    }

    public TraderAction getAction(){
        return this.action;
    }


    public String toString(){
        if(this.action == TraderAction.CONSULT){
            return succeed? String.valueOf(this.price):"Failed to get price";
        } else if(this.action == TraderAction.BUY){
            return succeed? "PURCHASE REQUEST SUCCEEDS":"PURCHASE REQUEST FAILS";
        } else {
            return succeed? "SELL REQUEST SUCCEEDS":"SELL REQUEST FAILS";
        }
    }


}

