package com.vam.handler;

import com.google.gson.Gson;
import com.vam.json.PeerToPeerAction;
import com.vam.json.PeerToPeerMessage;
import com.vam.peer.Peer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

/**
 * Peer- Super Peer Request
 */
public class PeerToPeerRequestHandler implements Runnable {

    private Logger logger = LoggerFactory.getLogger(PeerToPeerRequestHandler.class);
    private Socket client;
    private Peer peer;

    public PeerToPeerRequestHandler(Socket client, Peer peer) {
        this.client = client;
        this.peer = peer;
    }

    @Override
    public void run() {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter output = new PrintWriter(client.getOutputStream(), true);
            // add API code here
            // the below code is just for example, will change when API added.
            StringBuilder clientInputBuilder = new StringBuilder();
            String clientInput;
            while ((clientInput = input.readLine()) != null) {
                clientInputBuilder.append(clientInput);
            }
            Gson gson = new Gson();
            PeerToPeerMessage message = gson.fromJson(clientInputBuilder.toString(), PeerToPeerMessage.class);
            processPeerResponse(message);
            client.close();
        } catch (IOException e) {
            throw new RuntimeException("Could not connect to client in peer to peer request handler", e);
        }
    }

    private void processPeerResponse(PeerToPeerMessage message) {
        if (message.getAction() == PeerToPeerAction.JOIN_PEER_NETWORK) {
            logger.info("received request to joing a peer network");
            peer.addPeerToNetwork(message.getPeerData());
        } else if (message.getAction() == PeerToPeerAction.UPDATE_PEER_NETWORK) {
            logger.info("received a request to update my own network");
            peer.updateMyNetwork(message.getPeerNetwork());
        } else if (message.getAction() == PeerToPeerAction.FIND_MARKET) {
            if (peer.getIsSuper()) {
                if (message.getTraderRequest().getContinent().equals(peer.getContinent()) { // it is in our continent, so find the right person and send along
                    peer.findMarketInNetworkSendAlong(message);
                } else {
                    peer.superSendAlong(message);
                }
            } else { // the only way you get a find market and you are NOT super is if it is your market
                peer.processMarketAction(message);
            }
        } else if (message.getAction() == PeerToPeerAction.MARKET_RESPONSE) { // only ones who should get this are supers to pass along or peers to be endpoint
            if (peer.getIsSuper()) {
                if (message.getTraderRequest().getContinent().equals(peer.getContinent()) { // it is in our continent, so find the right person and send along
                    peer.findMarketInNetworkSendAlong(message);
                } else {
                    peer.superSendAlong(message);
                }
            } else {
                peer.processMarketResponse(message);
            }
        }
    }
}
