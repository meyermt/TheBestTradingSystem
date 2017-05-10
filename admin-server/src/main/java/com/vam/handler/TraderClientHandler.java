package com.vam.handler;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.vam.json.TraderLogin;

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
            // add API code here
            // the below code is just for example, will change when API added.
            String clientInput = input.readLine();
            Gson gson = new Gson();
            TraderLogin login = gson.fromJson(clientInput, TraderLogin.class);
            client.close();
        } catch (IOException e) {
            logger.error("Ran into an issue reading or writing from client {}", client.getPort(), e);
        }
    }
}
