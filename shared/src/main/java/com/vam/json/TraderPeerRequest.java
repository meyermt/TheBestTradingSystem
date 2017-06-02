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
    private Date date;
    private DateFormat dateFormat;
    private String trader;
    private TraderAction action;
    private String stock;
    private int shares;

    public TraderPeerRequest(){}

    public TraderPeerRequest(String date, DateFormat dateFormat, String traderName, TraderAction action, String stock, int shares){
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
