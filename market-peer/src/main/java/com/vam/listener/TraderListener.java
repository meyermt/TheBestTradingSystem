package com.vam.listener;


import com.google.gson.Gson;
import com.vam.peer.Peer;
import com.vam.json.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by VictoriatheEast on 6/4/17.
 */
public class TraderListener implements Runnable {

    private Peer peer;
    private ServerSocket traderSocket;
    private boolean exit = false;

    public TraderListener(Peer peer, ServerSocket traderSocket) {
        this.peer = peer;
        this.traderSocket = traderSocket;
    }

    @Override
    public void run() {
        while (!exit) {
            try {
                Socket client = traderSocket.accept();
                BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
                PrintWriter pw = new PrintWriter(client.getOutputStream(), true);
                // add API code here
                // the below code is just for example, will change when API added.
                String sourceMarket = peer.getMarket();
                TraderPeerRequest request = null;
                TraderPeerResponse response = null;
                PeerPeerResponse peerPeerResponse = null;
                Gson gson = new Gson();
                request = gson.fromJson(br.readLine(), TraderPeerRequest.class);

                //Process request
                if(request != null) {
                    if (request.getAction() == TraderAction.CONSULT) {
                        peerPeerResponse = peer.consult(request);
                       response = new TraderPeerResponse(true, peerPeerResponse.getAction(),peerPeerResponse.getPrice(),
                                peerPeerResponse.getStock(),peerPeerResponse.getShares());
                    } else {
                        peerPeerResponse = peer.transact(request);
                       response = new TraderPeerResponse(true, peerPeerResponse.getAction(),peerPeerResponse.getPrice(),
                               peerPeerResponse.getStock(),peerPeerResponse.getShares());
                   }
                } else {
                   System.out.println("The request from the trader is null");
                }

                //Send back response
                pw.println(gson.toJson(response));


            }catch (IOException e) {
                throw new RuntimeException("Could not open client for admin listening", e);
            }
        }
    }


    /**
     * Custom method to stop the server.
     */
    public void stopServer() {
        exit = true;
    }
}
