package com.vam.listener;

import com.google.gson.Gson;
import com.vam.handler.PeerToPeerRequestHandler;
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
                PeerToPeerRequestHandler handler = new PeerToPeerRequestHandler(client, peer);
                new Thread(handler).start();
            } catch (IOException e) {
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
