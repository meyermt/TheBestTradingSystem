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
    private String trader;
    private String sourceIP;
    private int sourcePort;
    private TraderAction action;
    private String stock;
    private double price;
    private int shares;

    public TraderPeerRequest(){}

    public TraderPeerRequest(String traderName, String sourceIP,int sourcePort, TraderAction action, String stock, double price, int shares){
            this.trader = traderName;
            this.action = action;
            this.stock = stock;
            this.price=price;
            this.shares = shares;
    }

    public TraderAction getAction() {
        return action;
    }

    public String getStock() {
        return stock;
    }

    public int getShares() {
        return shares;
    }

}
