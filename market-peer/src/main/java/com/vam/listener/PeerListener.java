package com.vam.listener;

import com.google.gson.Gson;
import com.vam.json.*;
import com.vam.peer.Peer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by michaelmeyer on 6/4/17.
 */
public class PeerListener implements Runnable {

    private Peer peer;
    private ServerSocket peerServerSocket;
    private boolean exit = false;

    public PeerListener(Peer peer, ServerSocket peerServerSocket) {
        this.peer = peer;
        this.peerServerSocket = peerServerSocket;
    }


    @Override
    public void run() {
        while (!exit) {
            try {
                Socket client = peerServerSocket.accept();
                BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
                PrintWriter output = new PrintWriter(client.getOutputStream(), true);
                // add API code here
                // the below code is just for example, will change when API added.
                StringBuilder clientInputBuilder = new StringBuilder();
                String clientInput;
                while ((clientInput = input.readLine()) != null) {
                    clientInputBuilder.append(clientInput);
                }
                Gson gson = new Gson();
                PeerToPeerMessage message = gson.fromJson(clientInputBuilder.toString(), PeerToPeerMessage.class);
                processPeerResponse(message);
                client.close();
            } catch (IOException e) {
                throw new RuntimeException("Could not open client for admin listening", e);
            }
        }
    }

    private void processPeerResponse(PeerToPeerMessage message) {
        if (message.getAction() == PeerToPeerAction.JOIN_PEER_NETWORK) {
            peer.addPeerToNetwork(message.getPeerData());
        } else if (message.getAction() == PeerToPeerAction.UPDATE_PEER_NETWORK) {
            peer.updateMyNetwork(message.getPeerNetwork());
        }
    }

    /**
     * Custom method to stop the server.
     */
    public void stopServer() {
        exit = true;
    }
}
