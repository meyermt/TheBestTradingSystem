package com.vam.server;

import com.vam.dao.PeersDAO;
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
    private final PeersDAO peersDB;
    private boolean exit;

    /**
     * Instantiates new peer server.
     * @param peerServer ServerSocket for listening to peer requests
     */
    public PeerServer(ServerSocket peerServer, PeersDAO peersDB) {
        this.peerServer = peerServer;
        this.peersDB = peersDB;
        exit = false;
    }

    @Override
    public void run() {
        try {
            while(!exit) {
                Socket client = peerServer.accept();
                new Thread(new PeerClientHandler(client, peersDB)).start();
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to connect with client socket", e);
        }
    }

    /**
     * Custom method to stop the server.
     */
    public void stopServer() {
        exit = true;
    }
}
