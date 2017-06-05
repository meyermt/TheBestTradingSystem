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

            TraderPeerRequest request = null;
            StringBuilder traderInputBuilder = new StringBuilder();
            String traderInput;
            while ((traderInput = br.readLine()) != null) {
                    traderInputBuilder.append(traderInput);
            }

            TraderPeerRequest traderPeerRequest = gson.fromJson(traderInputBuilder.toString(),TraderPeerRequest.class);
            TraderPeerResponse traderPeerResponse = null;
            if(traderPeerRequest.getMarket().equals(peer.getMarket())) {
                if(traderPeerRequest.getAction() == TraderAction.CONSULT) {
                    traderPeerResponse = peer.consultPriceLocally(request);
                } else {
                    traderPeerResponse = peer.transactLocally(request);
                }

                pw.println(gson.toJson(traderPeerResponse));

            } else {
                    PeerToPeerMessage peerToPeerMessage = new PeerToPeerMessage(PeerToPeerAction.FIND_MARKET,peer.getMarket(),
                            request.getMarket(),request.getContinent(),traderPeerRequest,null,null,null);
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

}

