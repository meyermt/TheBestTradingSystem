package com.vam.json;

import org.jgroups.Address;

/**
 * Created by michaelmeyer on 6/3/17.
 */
public class PeerPeerMessage {

    private PeerPeerAction action;
    private Address sourceAddr;
    private String sourceIP;
    private int sourcePort;
    private Address targetAddr;
    private String targetIP;
    private int targetPort;
    private String market;

    public PeerPeerMessage(PeerPeerAction action, String sourceIP, int sourcePort, String targetIP, int targetPort, String market) {
        this.action = action;
        this.sourceIP = sourceIP;
        this.sourcePort = sourcePort;
        this.targetIP = targetIP;
        this.targetPort = targetPort;
        this.market = market;
    }

    @Override
    public String toString() {
        return "PeerPeerMessage{" +
                "action=" + action +
                ", sourceIP='" + sourceIP + '\'' +
                ", sourcePort=" + sourcePort +
                ", targetIP='" + targetIP + '\'' +
                ", targetPort=" + targetPort +
                ", market='" + market + '\'' +
                '}';
    }

    public PeerPeerAction getAction() {
        return action;
    }

    public String getSourceIP() {
        return sourceIP;
    }

    public int getSourcePort() {
        return sourcePort;
    }

    public String getTargetIP() {
        return targetIP;
    }

    public int getTargetPort() {
        return targetPort;
    }

    public String getMarket() {
        return market;
    }
}
