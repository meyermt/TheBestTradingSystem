package com.vam.handler;

import com.vam.server.TraderServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Handles requests and messaging to peers. Will register super peers, broadcast to other superpeers,
 * tell new peers which superpeer to register with.
 * Created by michaelmeyer on 5/8/17.
 */
public class PeerClientHandler implements Runnable {

    private Logger logger = LoggerFactory.getLogger(PeerClientHandler.class);
    private Socket client;

    public PeerClientHandler(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter output = new PrintWriter(client.getOutputStream(), true);
            String clientInput;
            while ((clientInput = input.readLine()) != null) {
                // read json here
            }
            // add API code here
            client.close();
        } catch (IOException e) {
            logger.error("Ran into an issue reading or writing from client {}", client.getPort(), e);
        }
    }
}
