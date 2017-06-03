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

    public PeerPeerMessage(PeerPeerAction action, Address sourceAddr, String sourceIP, int sourcePort, Address targetAddr, String targetIP, int targetPort, String market) {
        this.action = action;
        this.sourceAddr = sourceAddr;
        this.sourceIP = sourceIP;
        this.sourcePort = sourcePort;
        this.targetAddr = targetAddr;
        this.targetIP = targetIP;
        this.targetPort = targetPort;
        this.market = market;
    }

    @Override
    public String toString() {
        return "PeerPeerMessage{" +
                "action=" + action +
                ", sourceAddr=" + sourceAddr +
                ", sourceIP='" + sourceIP + '\'' +
                ", sourcePort=" + sourcePort +
                ", targetAddr=" + targetAddr +
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

    public Address getSourceAddr() {
        return sourceAddr;
    }

    public Address getTargetAddr() {
        return targetAddr;
    }
}
