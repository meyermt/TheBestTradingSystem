package com.vam.model;

/**
 * Created by VictoriatheEast on 5/27/17.
 */
public class Trader {

    private String peerIP;
    private int peerPort;
    private String adminIP;
    private int adminPort;
    private boolean login = false;
    private String name;
    private String password;
    private String actionList;

    public Trader(String adminIP, int adminPort){
        this.adminIP = adminIP;
        this.adminPort = adminPort;
    }

    public Trader(String peerIP, int peerPort, String actionList){
        this.peerIP = peerIP;
        this.peerPort = peerPort;
        this.actionList = actionList;
    }

    public void start(){

    }


}
