package com.vam.json;


import java.util.Map;
import java.util.HashMap;

/**
 * PeerSPResponse class renders a response from the peer
 */
public class PeerSPResponse {

    private boolean succeed;
    private Map<String, Integer> peerMap;


    public PeerSPResponse(boolean succeed, Map<String, Integer> peerMap){
        this.succeed = succeed;
        this.peerMap = peerMap;

    }


    public boolean isSucceed() {
        return succeed;
    }

    public Map<String, Integer> getPeerMap(){
        return this.peerMap;
    }

}

