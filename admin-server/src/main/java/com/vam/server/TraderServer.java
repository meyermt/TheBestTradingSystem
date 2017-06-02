package com.vam.server;

import com.vam.Main;
import com.vam.dao.PeersDAO;
import com.vam.dao.StocksDAO;
import com.vam.handler.TraderClientHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Server for listening for requests from traders.
 * Created by michaelmeyer on 5/8/17.
 */
public class TraderServer implements Runnable {

    private Logger logger = LoggerFactory.getLogger(TraderServer.class);
    private final ServerSocket traderServer;
    private final StocksDAO stocksDB;
    private final PeersDAO peersDB;
    private boolean exit;

    /**
     * Instantiates a new trader server.
     * @param traderServer ServerSocket that listens for trader connections
     */
    public TraderServer(ServerSocket traderServer, StocksDAO stocksDB, PeersDAO peersDB) {
        this.traderServer = traderServer;
        this.stocksDB = stocksDB;
        this.peersDB = peersDB;
        exit = false;
    }

    @Override
    public void run() {
        try {
            while(!exit) {
                Socket client = traderServer.accept();
                System.out.println("heard something from someone");
                new Thread(new TraderClientHandler(client, stocksDB, peersDB)).start();
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
