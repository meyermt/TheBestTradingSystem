package com.vam.handler;

import com.google.gson.Gson;
import com.vam.dao.PeersDAO;
import com.vam.json.*;
import com.vam.server.TraderServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Collections;
import java.util.List;

/**
 * Handles requests and messaging to peers. Will register super peers, broadcast to other superpeers,
 * tell new peers which superpeer to register with.
 * Created by michaelmeyer on 5/8/17.
 */
public class PeerClientHandler implements Runnable {

    private Logger logger = LoggerFactory.getLogger(PeerClientHandler.class);
    private Socket client;
    private PeersDAO peersDB;

    public PeerClientHandler(Socket client, PeersDAO peersDB) {
        this.client = client;
        this.peersDB = peersDB;
    }

    @Override
    public void run() {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter output = new PrintWriter(client.getOutputStream(), true);
            // add API code here
            // the below code is just for example, will change when API added.
            String clientInput = input.readLine();
            Gson gson = new Gson();
            PeerRequest request = gson.fromJson(clientInput, PeerRequest.class);
            processPeerReq(request);
            client.close();
        } catch (IOException e) {
            logger.error("Ran into an issue reading or writing from client {}", client.getPort());
            throw new RuntimeException(e);
        }
    }

    private void processPeerReq(PeerRequest request) {
        Socket client = tryClient(request);
        if (request.getAction() == PeerAction.ENTER_NETWORK) {
            List<Peer> peers = peersDB.getContinentPeers(request.getContinent());
            peersDB.insertPeer(request.getSourceIP(), request.getSourcePort(), request.getContinent(), request.getCountry(), request.getMarket(), false);
            if (peers.isEmpty()) {
                PeerResponse response = new PeerResponse(PeerResponseCode.NO_NETWORK, Collections.emptyList(), Collections.emptyList());
                sendResponse(client, response);
            } else {
                PeerResponse response = new PeerResponse(PeerResponseCode.OK, peers, Collections.emptyList());
                sendResponse(client, response);
            }
        } else if (request.getAction() == PeerAction.ENTER_SP_NETWORK) {
            //List<Peer> superpeers = peersDB.getSuperPeers();
            // TODO: Need to add query (above) for sp's

        } else if (request.getAction() == PeerAction.NEW_SP) {
            // TODO: Need to add queries to undo existing SP (potentially wipe out entry of existing) and then update or just reinsert new SP
        }
        // TODO: Need another API entry for removing nodes. If nodes detect other nodes down should be reported
    }

    private void sendResponse(Socket client, PeerResponse response) {
        try {
            Gson gson = new Gson();
            PrintWriter output = new PrintWriter(client.getOutputStream(), true);
            //logger.info("sending response type: {} to {} ip and {} port", response.getCode(), response.getPeerIP(), response.getPeerPort());
            output.println(gson.toJson(response));
        } catch (IOException e) {
            throw new RuntimeException("Error sending response to trader", e);
        }
    }

    private Socket tryClient(PeerRequest request) {
        try {
            Socket client = new Socket(request.getSourceIP(), request.getSourcePort());
            return client;
        } catch (IOException e) {
            logger.error("Unable to secure connection back to trader at {} ip and {} port.", request.getSourceIP(), request.getSourcePort());
            throw new RuntimeException(e);
        }
    }
}
