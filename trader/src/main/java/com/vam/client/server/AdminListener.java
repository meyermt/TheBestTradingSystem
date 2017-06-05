package com.vam.client.server;

import com.google.gson.Gson;
import com.vam.TradePanel;
import com.vam.json.AdminPeerResponse;
import com.vam.json.AdminPeerResponseCode;
import com.vam.json.AdminTraderResponse;
import com.vam.json.AdminTraderResponseCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by michaelmeyer on 6/5/17.
 */
public class AdminListener implements Runnable {

    private ServerSocket socket;
    private TradePanel tradePanel;
    private boolean exit;
    private Logger logger = LoggerFactory.getLogger(AdminListener.class);

    public AdminListener(TradePanel tradePanel, ServerSocket socket) {
        this.tradePanel = tradePanel;
        this.socket = socket;
    }

    @Override
    public void run() {
        while (!exit) {
            try {
                Socket client = socket.accept();
                logger.info("we heard something!");
                BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
                PrintWriter output = new PrintWriter(client.getOutputStream(), true);
                // add API code here
                // the below code is just for example, will change when API added.
                StringBuilder clientInputBuilder = new StringBuilder();
                String clientInput;
                while ((clientInput = input.readLine()) != null) {
                    clientInputBuilder.append(clientInput);
                }
                logger.info("received: {}", clientInputBuilder.toString());
                Gson gson = new Gson();
                AdminTraderResponse response = gson.fromJson(clientInputBuilder.toString(), AdminTraderResponse.class);
                processAdminResponse(response);
                client.close();
            } catch (IOException e) {
                throw new RuntimeException("Could not open client for admin listening", e);
            }
        }
    }


    private void processAdminResponse(AdminTraderResponse response) {
        if (response.getCode() == AdminTraderResponseCode.OK) {
            if (!response.getStocks().isEmpty()) { // could have used another code but cheaty way to see if it was login
                logger.info("got a response and processing login");
                tradePanel.processLogin(response);
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
