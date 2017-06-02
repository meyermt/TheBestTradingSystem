package com.vam.json;

import java.util.List;

/**
 * Created by michaelmeyer on 5/23/17.
 */
public class AdminTraderResponse {

    private AdminTraderResponseCode code;
    private String peerIP;
    private int peerPort;
    private List<Stock> stocks;

    public AdminTraderResponse() {};

    public AdminTraderResponse(AdminTraderResponseCode code, String peerIP, int peerPort, List<Stock> stocks) {
        this.code = code;
        this.peerIP = peerIP;
        this.peerPort = peerPort;
        this.stocks = stocks;
    }

    public AdminTraderResponseCode getCode() {
        return code;
    }

    public String getPeerIP() {
        return peerIP;
    }

    public int getPeerPort() {
        return peerPort;
    }

    public List<Stock> getStocks() {
        return stocks;
    }
}
