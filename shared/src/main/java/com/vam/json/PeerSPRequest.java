package com.vam.json;

import java.util.Date;
import java.util.Map;
import java.util.HashMap;


/**
 * PeerSPRequest class renders and updates a request from the client and peer
 */
public class PeerSPRequest {

    private String market;


    public PeerSPRequest(String market){

        this.market = market;

    }

    public String getMarket() {
        return market;
    }



}
