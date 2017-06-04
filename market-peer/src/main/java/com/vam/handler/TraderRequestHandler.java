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
            TraderPeerResponse response = null;
            PeerPeerResponse peerPeerResponse = null;
            StringBuilder traderInputBuilder = new StringBuilder();
            String traderInput;
            while ((traderInput = br.readLine()) != null) {
                    traderInputBuilder.append(traderInput);
            }

            TraderPeerRequest traderPeerRequest = gson.fromJson(traderInputBuilder.toString(),TraderPeerRequest.class);
            if(traderPeerRequest.getMarket() == peer.getMarket()) {
                    if(traderPeerRequest.getAction() == TraderAction.CONSULT) {
                            peer.consultPrice();
                    } else if ()
            }

        } catch (IOException e) {
            throw new RuntimeException("Could not connect to trader in trader to peer request handler");
        }


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

