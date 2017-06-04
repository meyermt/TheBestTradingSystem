package com.vam.handler;

import java.io.*;
import java.net.Socket;

import com.google.gson.*;
import com.vam.json.*;
import com.vam.peer.Peer;


public class TraderRequestHandler implements Runnable {

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
            PeerSPRequest peerRequest = null;
            PeerSPResponse peerResponse = null;
            while (true) {
                //Get request from trader
                request = gson.fromJson(br.readLine(), TraderPeerRequest.class);



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

