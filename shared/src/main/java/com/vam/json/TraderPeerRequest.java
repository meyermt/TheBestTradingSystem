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
    private TraderAction action;
    private String stock;
    private int shares;


    public TraderPeerRequest(TraderAction action, String stock, int shares){

            this.action = action;
            this.stock = stock;
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
