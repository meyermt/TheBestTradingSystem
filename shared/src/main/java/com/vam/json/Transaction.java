package com.vam.json;

/**
 * Created by VictoriatheEast on 6/4/17. For transaction list
 */
public class Transaction {

    private int sourcePort;
    private TraderAction action;
    private String stockName;
    private double price;
    private int shares;


    public Transaction(int sourcePort, TraderAction action, String stockName, double price, int shares){
        this.sourcePort = sourcePort;
        this.action = action;
        this.stockName = stockName;
        this.price = price;
        this.shares = shares;
    }

    public String toString(){
        String transaction = this.action == TraderAction.BUY? "BUY" : "SELL";
        return sourcePort + " " + transaction + " "+ shares + " of" + stockName + " at price" + price + "\n";
    }
}
