package com.vam.json;

/**
 * Created by VictoriatheEast on 6/1/17.
 */


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Scanner;
import com.google.gson.*;

public class Trader {

    private static final Logger logger = LoggerFactory.getLogger(Trader.class);
    private static DateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm");

    private String peerIP;
    private int peerPort;
    //Todo
    //Deal with timezone later, Locale class
    private String timezone = null;
    //For my own testing purpose
    //Todo
    //Sending request from the trader to the peer
    private String requestFile;



    public Trader(String IP, int port, String file) {
        this.peerIP = IP;
        this.peerPort = port;
        this.requestFile = file;
    }

    public TraderPeerRequest generateRequest(String line){

        if(line == null){
            return new TraderPeerRequest();
        }

        String[] split = line.trim().split(",");

        if (split.length != 6) {
            logger.error("Wrong format: " + line);
            return new TraderPeerRequest();
        } else {

            TraderAction traderAction = null;
            String action = split[3];
            if(action.equals("BUY")){
                traderAction = TraderAction.BUY;
            } else if(action.equals("SELL")){
                traderAction = TraderAction.SELL;
            } else {
                traderAction = TraderAction.CONSULT;
            }

            return new TraderPeerRequest(split[0] + " " + split[1], format, split[2],traderAction, split[4],
                    Integer.parseInt(split[5]));
        }


    }

    private void process() throws IOException {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            logger.error("Error occurs in thread sleep.");
            e.printStackTrace();
        }

        Socket socket = new Socket(this.peerIP, this.peerPort);
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);

        Scanner sc = new Scanner(new File(requestFile));

        //Skip the table description
        String description = sc.nextLine();

        logger.info("PeerRequest begin!");
        Gson gson = new Gson();
        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();

            TraderPeerRequest request = generateRequest(line);
            String json = gson.toJson(request);
            pw.println(json);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            TraderPeerResponse response = gson.fromJson(br.readLine(),TraderPeerResponse.class);
            System.out.println(response.toString());
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        logger.info("Requests complete!");
        socket.close();
        sc.close();
    }

    public static void main(String[] args) throws IOException {

        String[] inputs = new String[3];
        //Parse command lines
        if (args.length == 3) {

            if (!args[0].contains("=") || !args[1].contains("=") || !args[2].contains("=")) {
                throw new IllegalArgumentException("Command Line Argument is in wrong format, " +
                        "java Trader --IP=127.0.0.1 --port=1000 --file=client_1_list.csv");
            } else {

                String[] split1 = args[0].split("=");
                String command1 = split1[0];
                String[] split2 = args[1].split("=");
                String command2 = split2[0];
                String[] split3 = args[2].split("=");
                String command3 = split3[0];


                if (!command1.equals("--IP") || !command2.equals("--port") || !command3.equals("--file")) {

                    throw new IllegalArgumentException("Command Line Argument is in wrong format, " +
                            "java Trader --IP=127.0.0.1 --port=2000 --file=client_1_list.csv");


                }

                inputs[0] = split1[1];
                inputs[1] = split2[1];
                inputs[2] = split3[1];

            }


        } else {

            throw new IllegalArgumentException("Command Line Argument is in wrong format, " +
                    "java Trader --IP=127.0.0.1 --port=2000 --file=client_1_list.csv");

        }

        Trader trader = new Trader(inputs[0],Integer.parseInt(inputs[1]),inputs[3]);
        trader.process();
    }


}

