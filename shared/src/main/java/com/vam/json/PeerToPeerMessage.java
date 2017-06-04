package com.vam.json;

import java.util.List;

/**
 * Created by michaelmeyer on 6/4/17.
 */
public class PeerToPeerMessage {

    private PeerToPeerAction action;
    private TraderPeerRequest traderRequest;
    private TraderPeerResponse response;
    private PeerData peerData;
    private List<PeerData> peerNetwork;

    public PeerToPeerMessage(PeerToPeerAction action, TraderPeerRequest traderRequest, TraderPeerResponse response, PeerData peerData, List<PeerData> peerNetwork) {
        this.action = action;
        this.traderRequest = traderRequest;
        this.response = response;
        this.peerData = peerData;
        this.peerNetwork = peerNetwork;
    }

    @Override
    public String toString() {
        return "PeerToPeerMessage{" +
                "action=" + action +
                ", traderRequest=" + traderRequest +
                ", response=" + response +
                ", peerData=" + peerData +
                ", peerNetwork=" + peerNetwork +
                '}';
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
