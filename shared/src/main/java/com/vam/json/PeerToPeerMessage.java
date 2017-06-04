package com.vam.json;

import java.util.List;

/**
 * Created by michaelmeyer on 6/4/17.
 */
public class PeerToPeerMessage {

    private PeerToPeerAction action;
    private PeerData peerData;
    private List<PeerData> peerNetwork;

    public PeerToPeerMessage(PeerToPeerAction action, PeerData peerData, List<PeerData> peerNetwork) {
        this.action = action;
        this.peerData = peerData;
        this.peerNetwork = peerNetwork;
    }

    @Override
    public String toString() {
        return "PeerToPeerMessage{" +
                "action=" + action +
                ", peerData=" + peerData +
                ", peerNetwork=" + peerNetwork +
                '}';
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
