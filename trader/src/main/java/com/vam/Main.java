package com.vam;

import com.vam.client.server.AdminListener;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by michaelmeyer on 5/1/17.
 */
public class Main implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(Main.class);
    private Thread mRenderThread;

    public static void main(String[] args) {
        System.out.println("Hey you're running the trader!");
        logger.info("You should see me log for trader!");
        //Manual
        Map<String, Integer> ports = loadPortOpts(args);
        for (Integer port : ports.values()) {
            Main trader = new Main(port);
        }

        //Automatic
        


    }

    private static Map<String, Integer> loadPortOpts(String[] args) {
        Options options = new Options();
        Option traderOpt = new Option("tp", TRADER_PORT, true, "TraderAdminRequest listening port to run on");
        //Option peerOpt = new Option("pp", PEER_PORT, true, "Peer listening port to run on");
        traderOpt.setRequired(true);
        peerOpt.setRequired(true);
        options.addOption(traderOpt);
        options.addOption(peerOpt);
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
            Map<String, Integer> ports = new HashMap<>();
            Integer trader = Integer.parseInt(cmd.getOptionValue(TRADER_PORT));
            Integer peer = Integer.parseInt(cmd.getOptionValue(PEER_PORT));
            ports.put(TRADER_PORT, trader);
            ports.put(PEER_PORT, peer);
            return ports;
        } catch (ParseException e) {
            formatter.printHelp("admin server help", options);
            throw new RuntimeException("Unable to read arguments, see help.");
        }
    }

    public Main(int port1, int port2) {
        TradeFrame frame = new TradeFrame(this, port1, port2);
    }


    @Override
    public void run() {
        Main trader = new Main();
        trader.startRendering();
    }

    public void startRendering() {

        if (this.mRenderThread == null) {
            //All threads that are created in java need to be passed a Runnable object.
            //In this case we are making the "Runnable Object" the actual game instance.
            this.mRenderThread = new Thread(this);
            //Start the thread
            this.mRenderThread.start();
        }
    }



}