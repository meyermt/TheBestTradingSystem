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
public class Main {

    private static final String PEER_PORT = "peerPort";
    private static final String ADMIN_PORT = "adminPort";

    private static Logger logger = LoggerFactory.getLogger(Main.class);
    private Thread mRenderThread;

    public static void main(String[] args) {
        System.out.println("Hey you're running the trader!");
        logger.info("You should see me log for trader!");
        //Manual
        Map<String, Integer> ports = loadPortOpts(args);
        Main trader = new Main(ports.get(ADMIN_PORT), ports.get(PEER_PORT));
        //Automatic


    }

    private static Map<String, Integer> loadPortOpts(String[] args) {
        Options options = new Options();
        Option adminOpt = new Option("ap", ADMIN_PORT, true, "Admin listening port to run on");
        Option peerOpt = new Option("pp", PEER_PORT, true, "Peer listening port to run on");
        adminOpt.setRequired(true);
        peerOpt.setRequired(true);
        options.addOption(adminOpt);
        options.addOption(peerOpt);
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
            Map<String, Integer> ports = new HashMap<>();
            Integer adminPort = Integer.parseInt(cmd.getOptionValue(ADMIN_PORT));
            System.out.println("admin port is " + adminPort);
            Integer peerPort = Integer.parseInt(cmd.getOptionValue(PEER_PORT));
            System.out.println("peer port is " + peerPort);
            ports.put(ADMIN_PORT, adminPort);
            ports.put(PEER_PORT, peerPort);
            return ports;
        } catch (ParseException e) {
            formatter.printHelp("admin server help", options);
            throw new RuntimeException("Unable to read arguments, see help.");
        }
    }

    public Main(int adminPort, int peerPort) {
        TradeFrame frame = new TradeFrame(this, adminPort, peerPort);
    }

}