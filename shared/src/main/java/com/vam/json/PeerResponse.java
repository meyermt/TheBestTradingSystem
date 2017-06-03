package com.vam.json;


import org.jgroups.Address;

/**
 * PeerResponse class renders a response from the peer
 */
public class PeerResponse{

    private boolean succeed;
    private double price;
    private TraderAction action;
    private Address exchange;

    public PeerResponse(){}

    public PeerResponse(boolean succeed){
        this.succeed = succeed;
    }

    public PeerResponse(boolean succeed, TraderAction action, double price){
        this.succeed = succeed;
        this.action = action;
        this.price = price;

    }

    public PeerResponse(boolean succeed, double price, TraderAction action, Address exchange) {
        this.succeed = succeed;
        this.action = action;
        this.price = price;
        this.exchange = exchange;
    }

    public boolean isSucceed() {
        return succeed;
    }

    public TraderAction getAction(){
        return this.action;
    }

    public double getPrice() {
        return price;
    }

    public Address getExchange() {
        return exchange;
    }

}

