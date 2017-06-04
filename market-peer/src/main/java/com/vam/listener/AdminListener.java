package com.vam.listener;

import com.google.gson.Gson;
import com.vam.json.AdminPeerResponse;
import com.vam.json.AdminPeerResponseCode;
import com.vam.json.PeerAdminRequest;
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
public class AdminListener implements Runnable {

    private Peer peer;
    private ServerSocket adminServerSocket;
    private boolean exit = false;

    public AdminListener(Peer peer, ServerSocket adminServerSocket) {
        this.peer = peer;
        this.adminServerSocket = adminServerSocket;
    }

    @Override
    public void run() {
        while (!exit) {
            try {
                Socket client = adminServerSocket.accept();
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
                AdminPeerResponse response = gson.fromJson(clientInputBuilder.toString(), AdminPeerResponse.class);
                processAdminResponse(response);
                client.close();
            } catch (IOException e) {
                throw new RuntimeException("Could not open client for admin listening", e);
            }
        }
    }

    private void processAdminResponse(AdminPeerResponse response) {
        if (response.getCode() == AdminPeerResponseCode.OK) {
            if (!response.getSuperpeers().isEmpty()) { // this implies you are a superpeer and are getting your list of other superpeers
                peer.setSuperpeerNetwork(response.getSuperpeers());
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
