package com.vam.server;

import com.vam.handler.PeerClientHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Server for listening for requests from peers.
 * Created by michaelmeyer on 5/8/17.
 */
public class PeerServer implements Runnable {

    private Logger logger = LoggerFactory.getLogger(PeerServer.class);
    private final ServerSocket peerServer;
    private boolean exit;

    /**
     * Instantiates new peer com.vam.server.
     * @param peerServer ServerSocket for listening to peer requests
     */
    public PeerServer(ServerSocket peerServer) {
        this.peerServer = peerServer;
        exit = false;
    }

    @Override
    public void run() {
        try {
            while(!exit) {
                Socket client = peerServer.accept();
                new Thread(new PeerClientHandler(client)).start();
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to connect with client socket", e);
        }
    }

    /**
     * Custom method to stop the com.vam.server.
     */
    public void stopServer() {
        exit = true;
    }
}
