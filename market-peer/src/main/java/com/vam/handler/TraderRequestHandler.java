package com.vam.handler;

import java.io.*;
import java.net.Socket;

import com.google.gson.*;
import com.vam.json.*;
import com.vam.peer.Peer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TraderRequestHandler implements Runnable {

    private Logger logger = LoggerFactory.getLogger(TraderRequestHandler.class);
    private Socket socket;
    private Peer peer;

    public TraderRequestHandler(Socket socket, Peer peer) {
        this.socket = socket;
        this.peer = peer;
    }



    @Override
    public void run() {
        try {
            Gson gson = new Gson();
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);

            StringBuilder traderInputBuilder = new StringBuilder();
            String traderInput;
            while ((traderInput = br.readLine()) != null) {
                    traderInputBuilder.append(traderInput);
            }
            logger.info(traderInputBuilder.toString());

            TraderPeerRequest traderPeerRequest = gson.fromJson(traderInputBuilder.toString(),TraderPeerRequest.class);
            TraderPeerResponse traderPeerResponse = null;
            if(traderPeerRequest.getMarket().equals(peer.getMarket())) {
                if(traderPeerRequest.getAction() == TraderAction.CONSULT) {
                    traderPeerResponse = peer.consultPriceLocally(traderPeerRequest);
                } else {
                    traderPeerResponse = peer.transactLocally(traderPeerRequest);
                }

                //pw.println(gson.toJson(traderPeerResponse));
                Socket respClient = tryClient(traderPeerRequest.getSourceIP(), traderPeerRequest.getSourcePort());
                sendResponse(respClient, traderPeerResponse);
                logger.info(traderPeerResponse.toString());

            } else {
                    PeerToPeerMessage peerToPeerMessage = new PeerToPeerMessage(PeerToPeerAction.FIND_MARKET,peer.getMarket(),
                            traderPeerRequest.getMarket(),traderPeerRequest.getContinent(),traderPeerRequest,null,null,null);
                    peer.processMarketAction(peerToPeerMessage);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not connect to trader in trader to peer request handler");
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendResponse(Socket client, TraderPeerResponse response) {
        try {
            Gson gson = new Gson();
            PrintWriter output = new PrintWriter(client.getOutputStream(), true);
            output.println(gson.toJson(response));
        } catch (IOException e) {
            throw new RuntimeException("Error sending response to trader", e);
        }
    }

    private Socket tryClient(String ip, int port) {
        try {
            Socket client = new Socket(ip, port);
            return client;
        } catch (IOException e) {
            logger.error("Unable to secure connection back to trader at {} ip and {} port.", ip, port);
            throw new RuntimeException(e);
        }
    }

}

