package com.vam.client.server;

import com.google.gson.Gson;
import com.vam.json.AdminTraderResponse;
import com.vam.json.Stock;
import com.vam.json.TraderAdminAction;
import com.vam.json.TraderAdminRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

/**
 * Created by michaelmeyer on 6/2/17.
 */
public class TraderAdminClient {

    private static Logger logger = LoggerFactory.getLogger(TraderAdminClient.class);
    private static String ADMIN_IP = "127.0.0.1";
    private static int ADMIN_PORT = 8089; // ok, guess we are deciding this is the port admin listens on for traders?

    public TraderAdminClient() {

    }

    public List<Stock> getStocks() {
        Socket client = tryClient();
        String country = "USA"; // TODO: Need to build this into the client so that it just gets sent into getStocks
        TraderAdminRequest request = new TraderAdminRequest("127.0.0.1", 8081, TraderAdminAction.LOGIN, country, "");
        return sendRequest(client, request).getStocks();
    }

    private AdminTraderResponse sendRequest(Socket client, TraderAdminRequest request) {
        try {
            Gson gson = new Gson();
            PrintWriter output = new PrintWriter(client.getOutputStream(), true);
            logger.info("sending request type: {}", request.getAction());
            output.println(gson.toJson(request));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String clientInput = bufferedReader.readLine();
            return gson.fromJson(clientInput, AdminTraderResponse.class);
        } catch (IOException e) {
            throw new RuntimeException("Error sending response to trader", e);
        }
    }

    private Socket tryClient() {
        try {
            Socket client = new Socket(ADMIN_IP, ADMIN_PORT);
            client.setKeepAlive(true);
            return client;
        } catch (IOException e) {
            logger.error("Unable to secure connection back to admin server.");
            throw new RuntimeException(e);
        }
    }
}
