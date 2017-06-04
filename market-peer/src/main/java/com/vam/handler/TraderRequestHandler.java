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
            TraderPeerResponse response = null;
            PeerPeerResponse peerPeerResponse = null;

            while (true) {
                //Get request from trader
                request = gson.fromJson(br.readLine(), TraderPeerRequest.class);

                //Process request
                if(request != null) {
                    if (request.getAction() == TraderAction.CONSULT) {
                        peerPeerResponse = peer.consult(request);
                        response = new TraderPeerResponse(true, peerPeerResponse.getAction(),peerPeerResponse.getPrice(),
                                peerPeerResponse.getStock());
                    } else {
                        peerPeerResponse = peer.transact(request);
                        response = new TraderPeerResponse(true, peerPeerResponse.getAction(),peerPeerResponse.getPrice(),
                                peerPeerResponse.getStock());
                    }
                } else {
                    System.out.println("The request from the trader is null");
                }

                //Send back response
                pw.println(gson.toJson(response));

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

