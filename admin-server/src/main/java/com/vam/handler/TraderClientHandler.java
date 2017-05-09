package com.vam.handler;

import com.vam.server.TraderServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles requests and messaging to traders. Should assign peer ip and port to incoming login requests
 * and give a list of stock inventory available for purchase.
 * Created by michaelmeyer on 5/8/17.
 */
public class TraderClientHandler implements Runnable{

    private Logger logger = LoggerFactory.getLogger(TraderClientHandler.class);
    public Socket client;

    /**
     * Instantiates new trader client handler.
     * @param client socket connection to client
     */
    public TraderClientHandler(Socket client) {
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
