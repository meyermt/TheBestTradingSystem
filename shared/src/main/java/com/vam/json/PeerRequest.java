package com.vam.json;

import java.io.*;
import java.util.Date;

/**
 * PeerRequest class renders and updates a request from the client and peer
 */
public class PeerRequest implements Serializable{

    private Date date;
    private String trader;
    private TraderAction action;
    private String stock;
    private int shares;


    public PeerRequest(Date date, String traderName, TraderAction action, String stock, int shares){

        this.date = date;
        this.trader = traderName;
        this.action = action;
        this.stock = stock;
        this.shares = shares;


    }

    public Date getDate() {
        return date;
    }

    public String getTrader() {
        return trader;
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
