package com.vam.json;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

/**
 * Request class renders and updates a request from the client and peer
 */
public class Request implements Serializable {

    private Date date;
    private DateFormat dateFormat;
    private String trader;
    private String action;
    private String stock;
    private int shares;
    private double capital;
    private boolean isExchange;

    public Request(){}

    public Request(String date, DateFormat dateFormat, String traderName, String action, String stock, int shares){
        try{
            this.date = dateFormat.parse(date);
            this.trader = traderName;
            this.action = action;
            this.stock = stock;
            this.shares = shares;

        } catch (ParseException e){
            e.printStackTrace();
        }

    }

    public Date getDate() {
        return date;
    }

    public String getTrader() {
        return trader;
    }

    public String getIntention() {
        return action;
    }

    public String getStock() {
        return stock;
    }

    public int getShares() {
        return shares;
    }

    public double getCapital() {
        return capital;
    }

    public boolean isExchange() {
        return isExchange;
    }
}
