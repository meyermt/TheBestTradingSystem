package com.vam.json;

/**
 * Created by VictoriatheEast on 6/2/17.
 */


import org.jgroups.Address;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

public class Cluster extends ReceiverAdapter{

    private View clusterView;

    private SuperPeerAdapter superPeer;
    private ConcurrentHashMap<String, Address> peerRegistrationMap;
    private ConcurrentHashMap<Address, String> addressRegistrationMap;
    private ExecutorService pool = null;

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    public Cluster(SuperPeerAdapter superPeer) {
        this.superPeer = superPeer;
        this.peerRegistrationMap = superPeer.getPeerRegistration();
        this.addressRegistrationMap = superPeer.getAddressRegistration();
        this.pool = superPeer.getPool();
    }

    @Override
    public void viewAccepted(View view) {
        System.out.println(view);
        if (clusterView == null) {
            clusterView= view;
            return;
        }

        List<Address> leftMembers = null;
        List<Address> joinedMembers = null;
        if (clusterView.size() > view.size()) {
            leftMembers = View.leftMembers(clusterView, view);
        } else if (clusterView.size() < view.size()) {
            joinedMembers = View.leftMembers(view, clusterView);
        }

        if (leftMembers != null) {
            for (Address address : leftMembers) {
                if (this.addressRegistrationMap.containsKey(address)) {
                    String peer = this.addressRegistrationMap.get(address);
                    this.addressRegistrationMap.remove(address);
                    this.peerRegistrationMap.remove(peer);
                    logger.info(peer + " left the " + superPeer.getContinent() + "cluster.");
                }
            }
        }

        // recover exchange server
        //joined members for recovery


        clusterView = view;

    }




}
