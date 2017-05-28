package com.vam.model;

import org.jgroups.JChannel;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.blocks.RpcDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jgroups.Address;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Market-Peer class
 */
public class Peer extends ReceiverAdapter {

    private static final Logger logger = LoggerFactory.getLogger(Peer.class);
    private static Peer peer;

    private int port;
    private String continent;
    private String country;
    private String market;
    private boolean isSuper;
    private String qtyFile;
    private String priceFile;

    private ConcurrentHashMap.KeySetView<Address, Boolean> sentinelAddSet = ConcurrentHashMap.newKeySet();

    protected JChannel channel;
    protected View view;
    protected RpcDispatcher rpcDispatcher;

    public Peer(){}

    public Peer(int port, String continent,String country, String market, boolean isSuper,
                String qtyFile, String priceFile){

        this.port = port;
        this.continent = continent;
        this.market = market;
        this.isSuper = false;
        this.qtyFile = qtyFile;
        this.priceFile = priceFile;

        try {
            String config = "main/resources/"+this.continent+".xml";
            this.channel = new JChannel(new File(config));
            this.channel.setName(this.market);
            this.channel.setReceiver(this);
            this.channel.setDiscardOwnMessages(true);
            this.channel.connect(this.continent);
            this.rpcDispatcher = new RpcDispatcher(this.channel,this,this,this);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setPort(String portNumber){
        this.port = Integer.parseInt(portNumber);
    }

    //I am thinking of having some sample price and stock files rather than the original ones. We can confirm with him.
    private void readPriceFile() {
        //Todo
    }

    private void readQtyFile() {
        //Todo
    }

    public void paxos(){

    }

    @Override
    public void viewAccepted(View newView){
        System.out.println(this.view);
        if(this.view == null){
            this.view = newView;
        }

        List<Address> membersLeaving = new ArrayList<>();
        List<Address> membersJoining = new ArrayList<>();

        if(this.view.size() > newView.size()){
            membersLeaving = View.leftMembers(this.view,newView);
        } else if(this.view.size() < newView.size()){
            membersJoining = View.leftMembers(newView,this.view);
        }

        if(membersLeaving != null){
            for(Address address : membersLeaving){

            }
        }

        if(membersJoining != null){
            for(Address address : membersJoining){

            }
        }

        this.view = newView;

    }



}
