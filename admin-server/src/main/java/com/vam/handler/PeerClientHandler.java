package com.vam.handler;

import com.google.gson.Gson;
import com.vam.dao.PeersDAO;
import com.vam.json.*;
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
            PeerAdminRequest request = gson.fromJson(clientInput, PeerAdminRequest.class);
            processPeerReq(request);
            client.close();
        } catch (IOException e) {
            logger.error("Ran into an issue reading or writing from client {}", client.getPort());
            throw new RuntimeException(e);
        }
    }

    private void processPeerReq(PeerAdminRequest request) {
        Socket client = tryClient(request);
        if (request.getAction() == PeerAdminAction.REGISTER_NETWORK) { // new network, so just wipe out old entries and add all new AND send back sps to superpeer
            logger.info("a superpeer is registering their network");
            peersDB.deleteContinentPeers(request.getContinent()); // wipe out old local peer network
            List<PeerData> superPeers = peersDB.getSuperPeers(); // collect remaining superpeers
            request.getPeers().forEach(peer ->{
                // TODO: need to confirm that the super peer will NOT send themselves in this list of peers in their registered network
                peersDB.insertPeer(peer.getIp(), peer.getPeerPort(), peer.getTraderPort(), peer.getContinent(), peer.getCountry(), peer.getMarket(), false);
            });
            peersDB.insertPeer(request.getSourceIP(), request.getSourcePeerPort(), request.getSourceTraderPort(), request.getContinent(), request.getCountry(), request.getMarket(), true);// assumes sp doesn't send themselves in list
            AdminPeerResponse response = new AdminPeerResponse(AdminPeerResponseCode.OK, superPeers);
            sendResponse(client, response);
        } else if (request.getAction() == PeerAdminAction.ADD_PEER) {
            logger.info("adding a new peer");
            request.getPeers().forEach(peer ->{
                peersDB.insertPeer(peer.getIp(), peer.getPeerPort(), peer.getTraderPort(), peer.getContinent(), peer.getCountry(), peer.getMarket(), false);
            });
            AdminPeerResponse response = new AdminPeerResponse(AdminPeerResponseCode.OK, Collections.emptyList());
            sendResponse(client, response);
        } else if (request.getAction() == PeerAdminAction.DELETE_PEER) {
            logger.info("deleting a peer");
            request.getPeers().forEach(peer ->{
                peersDB.deleteMarketPeer(peer.getMarket());
            });
            AdminPeerResponse response = new AdminPeerResponse(AdminPeerResponseCode.OK, Collections.emptyList());
        }
    }

    private void sendResponse(Socket client, AdminPeerResponse response) {
        try {
            logger.info("returning {} to peer", response.toString());
            Gson gson = new Gson();
            PrintWriter output = new PrintWriter(client.getOutputStream(), true);
            output.println(gson.toJson(response));
        } catch (IOException e) {
            throw new RuntimeException("Error sending response to trader", e);
        }
    }

    private Socket tryClient(PeerAdminRequest request) {
        try {
            logger.info("received {} from peer", request.toString());
            Socket client = new Socket(request.getSourceIP(), request.getSourcePeerPort());
            return client;
        } catch (IOException e) {
            logger.error("Unable to secure connection back to trader at {} ip and {} port.", request.getSourceIP(), request.getSourcePeerPort());
            throw new RuntimeException(e);
        }
    }
}