package com.vam.json;

import java.io.Serializable;
import org.jgroups.Address;

/**
 * Response class renders a response from the peer
 */
public class Response implements Serializable{

    private String message;
    private boolean Succeed;
    private double price;
    private double capital;
    private Address exchange;

    public Response(){}

    public Response(String message, boolean succeed, double price, double capital, Address exchange) {
        this.message = message;
        Succeed = succeed;
        this.price = price;
        this.capital = capital;
        this.exchange = exchange;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSucceed() {
        return Succeed;
    }

    public double getPrice() {
        return price;
    }

    public double getCapital() {
        return capital;
    }

    public Address getExchange() {
        return exchange;
    }
}
