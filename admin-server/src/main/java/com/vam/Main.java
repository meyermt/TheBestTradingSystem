package com.vam;

import com.vam.server.PeerServer;
import com.vam.server.TraderServer;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

/**
 * Main driver for admin com.vam.server module. Takes two ports as arguments and uses them to run two listening
 * com.vam.server threads.
 * Created by michaelmeyer on 5/1/17.
 */
public class Main {

    private static Logger logger = LoggerFactory.getLogger(Main.class);
    private static final String TRADER_PORT = "traderPort";
    private static final String PEER_PORT = "peerPort";

    /**
     * Takes port string args, starts two servers.
     * @param args com.vam.server options/arguments
     */
    public static void main(String[] args) {
        logger.info("Initializing admin-com.vam.server");
        Map<String, Integer> ports = loadPortOpts(args);
        try {
            logger.info("Running peer port on {}", ports.get(PEER_PORT));
            ServerSocket peerServer = new ServerSocket(ports.get(PEER_PORT));
            new Thread(new PeerServer(peerServer)).start();
            logger.info("Running trader port on {}", ports.get(TRADER_PORT));
            ServerSocket traderServer = new ServerSocket(ports.get(TRADER_PORT));
            new Thread(new TraderServer(traderServer)).start();
        } catch (IOException e) {
            throw new RuntimeException("Unable to load new com.vam.server.", e);
        }
    }

    private static Map<String, Integer> loadPortOpts(String[] args) {
        Options options = new Options();
        Option traderOpt = new Option("tp", TRADER_PORT, true, "Trader listening port to run on");
        Option peerOpt = new Option("pp", PEER_PORT, true, "Peer listening port to run on");
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
            formatter.printHelp("admin com.vam.server help", options);
            throw new RuntimeException("Unable to read arguments, see help.");
        }
    }

}
