package com.vam.json;

import java.io.*;
import java.net.Socket;

import com.google.gson.*;


public class TraderRequestHandler implements Runnable {

    private Socket socket;
    private PeerAdapter peer;

    public TraderRequestHandler(Socket socket, PeerAdapter peer) {
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
            PeerRequest peerRequest = null;
            PeerResponse peerResponse = null;
            while (true) {
                //Get request from trader
                request = gson.fromJson(br.readLine(), TraderPeerRequest.class);

                peerRequest = new PeerRequest(request.getDate(),request.getTrader(),request.getAction(),
                        request.getStock(),request.getShares());


                //Process request
                if(peerRequest.getAction() == TraderAction.CONSULT){
                    peerResponse = peer.consult(peerRequest);
                } else {
                    peerResponse = peer.transact(peerRequest);
                }

                TraderPeerResponse traderResponse = new TraderPeerResponse(peerResponse.isSucceed(),
                        peerResponse.getAction(),peerResponse.getPrice());

                //Send back response
                pw.println(gson.toJson(traderResponse));

            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

