package com.vam.json;

import java.io.Serializable;
import org.jgroups.Address;

/**
 * PeerResponse class renders a response from the peer
 */
public class PeerResponse implements Serializable{

    private boolean succeed;
    private double price;
    private TraderAction action;
    private Address peerAddress;
    private String message;

    public PeerResponse(){}

    public PeerResponse(boolean succeed){
        this.succeed = succeed;
    }

    public PeerResponse(boolean succeed, String message){
        this.succeed = succeed;
        this.message = message;
    }

    public PeerResponse(boolean succeed, TraderAction action, double price, String message){
        this.succeed = succeed;
        this.action = action;
        this.price = price;
        this.message = message;

    }

    public PeerResponse(boolean succeed, double price, TraderAction action, Address add) {
        this.succeed = succeed;
        this.action = action;
        this.price = price;
        this.peerAddress = add;
    }

    public boolean isSucceed() {
        return succeed;
    }

    public void setSucceed(boolean succeed){
        this.succeed = succeed;
    }

    public void setMessage(String msg){
        this.message = msg;
    }

    public String getMessge(){
        return this.message;
    }

    public TraderAction getAction(){
        return this.action;
    }

    public double getPrice() {
        return price;
    }

    public Address getPeer() {
        return peerAddress;
    }

    public void setAddress(Address add){
        this.peerAddress = add;

    }

}

