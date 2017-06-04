package com.vam.json;

import java.util.List;

/**
 * Created by michaelmeyer on 6/4/17.
 */
public class PeerToPeerMessage {

    private PeerToPeerAction action;
    private String sourceMarket;
    private String targetMarket;
    private String targetContinent;
    private TraderPeerRequest traderRequest;
    private TraderPeerResponse response;
    private PeerData peerData;
    private List<PeerData> peerNetwork;

    public PeerToPeerMessage(PeerToPeerAction action, String sourceMarket, String targetMarket, String targetContinent, TraderPeerRequest traderRequest, TraderPeerResponse response, PeerData peerData, List<PeerData> peerNetwork) {
        this.action = action;
        this.sourceMarket = sourceMarket;
        this.targetMarket = targetMarket;
        this.targetContinent = targetContinent;
        this.traderRequest = traderRequest;
        this.response = response;
        this.peerData = peerData;
        this.peerNetwork = peerNetwork;
    }

    @Override
    public String toString() {
        return "PeerToPeerMessage{" +
                "action=" + action +
                ", sourceMarket='" + sourceMarket + '\'' +
                ", targetMarket='" + targetMarket + '\'' +
                ", targetContinent='" + targetContinent + '\'' +
                ", traderRequest=" + traderRequest +
                ", response=" + response +
                ", peerData=" + peerData +
                ", peerNetwork=" + peerNetwork +
                '}';
    }

    public String getTargetContinent() {
        return targetContinent;
    }

    public String getSourceMarket() {
        return sourceMarket;
    }

    public String getTargetMarket() {
        return targetMarket;
    }

    public TraderPeerResponse getResponse() {
        return response;
    }

    public TraderPeerRequest getTraderRequest() {
        return traderRequest;
    }

    public PeerToPeerAction getAction() {
        return action;
    }

    public PeerData getPeerData() {
        return peerData;
    }

    public List<PeerData> getPeerNetwork() {
        return peerNetwork;
    }
}
