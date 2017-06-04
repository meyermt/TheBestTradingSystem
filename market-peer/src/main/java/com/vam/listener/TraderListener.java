package com.vam.listener;


import com.google.gson.Gson;
import com.vam.handler.TraderRequestHandler;
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
                TraderRequestHandler handler = new TraderRequestHandler(client,peer);
                new Thread(handler).start();
            } catch (IOException e) {
                throw new RuntimeException("Could not open peer for trader listening", e);
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