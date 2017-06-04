package com.vam.handler;

import com.google.gson.Gson;
import com.vam.json.*;
import com.vam.dao.PeersDAO;
import com.vam.dao.StocksDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Handles requests and messaging to traders. Should assign peer ip and port to incoming login requests
 * and give a list of stock inventory available for purchase.
 * Created by michaelmeyer on 5/8/17.
 */
public class TraderClientHandler implements Runnable{

    private Logger logger = LoggerFactory.getLogger(TraderClientHandler.class);
    public Socket client;
    public StocksDAO stocksDB;
    public PeersDAO peersDB;

    /**
     * Instantiates new trader client handler.
     * @param client socket connection to client
     */
    public TraderClientHandler(Socket client, StocksDAO stocksDB, PeersDAO peersDB) {
        this.client = client;
        this.stocksDB = stocksDB;
        this.peersDB = peersDB;
    }

    @Override
    public void run() {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter output = new PrintWriter(client.getOutputStream(), true);
            StringBuilder clientInputBuilder = new StringBuilder();
            String clientInput;
            while ((clientInput = input.readLine()) != null) {
                clientInputBuilder.append(clientInput);
            }
            Gson gson = new Gson();
            TraderAdminRequest request = gson.fromJson(clientInputBuilder.toString(), TraderAdminRequest.class);
            System.out.println("Got here gson");
            processTraderReq(request, client);
            client.close();
        } catch (IOException e) {
            logger.error("Ran into an issue reading or writing from client {}", client.getPort());
            throw new RuntimeException(e);
        }
    }

    private void processTraderReq(TraderAdminRequest request, Socket client) {
        peersDB.insertPeer("127.0.0.1", 8090, "America", "USA", "New York Stock Exchange", false);
        //Socket client = tryClient(request);
        System.out.println("Got here");
        if (request.getAction() == TraderAdminAction.LOGIN) {
            List<PeerData> peers = peersDB.getCountryPeers(request.getCountry());
            if (peers.isEmpty()) {
                AdminTraderResponse response = new AdminTraderResponse(AdminTraderResponseCode.NO_AVAILABLE_PEER, "", 0, Collections.emptyList());
                sendResponse(client, response);
            } else  {
                Random rn = new Random();
                int peerNum = rn.nextInt(peers.size()); // randomize which peer to connect to in a country
                PeerData connectPeer = peers.get(peerNum);
                List<Stock> stocks = stocksDB.getAllStocks();
                AdminTraderResponse response = new AdminTraderResponse(AdminTraderResponseCode.OK, connectPeer.getIp(), connectPeer.getPort(), stocks);
                sendResponse(client, response);
            }
        } else if (request.getAction() == TraderAdminAction.LOGOUT) {
            // TODO: fill this in. Not sure when/if this is used.
        } else if (request.getAction() == TraderAdminAction.PEER_FAILURE) {
            peersDB.deleteMarketPeer(request.getFailedPeerMarket());
            AdminTraderResponse response = new AdminTraderResponse(AdminTraderResponseCode.OK, "", 0, Collections.emptyList());
            sendResponse(client, response);
        } else {
            AdminTraderResponse response = new AdminTraderResponse(AdminTraderResponseCode.INVALID_ACTION, "", 0, Collections.emptyList());
            sendResponse(client, response);
        }
    }

    private void sendResponse(Socket client, AdminTraderResponse response) {
        try {
            Gson gson = new Gson();
            PrintWriter output = new PrintWriter(client.getOutputStream(), true);
            logger.info("sending response type: {} to {} ip and {} port", response.getCode(), response.getPeerIP(), response.getPeerPort());
            output.println(gson.toJson(response));
        } catch (IOException e) {
            throw new RuntimeException("Error sending response to trader", e);
        }
    }

    private Socket tryClient(TraderAdminRequest request) {
        try {
            Socket client = new Socket(request.getSourceIP(), request.getSourcePort());
            return client;
        } catch (IOException e) {
            logger.error("Unable to secure connection back to trader at {} ip and {} port.", request.getSourceIP(), request.getSourcePort());
            throw new RuntimeException(e);
        }
    }
}