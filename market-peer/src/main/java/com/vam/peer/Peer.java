package com.vam.peer;

import com.google.gson.Gson;
import com.vam.handler.TraderRequestHandler;
import com.vam.json.*;
import org.apache.commons.cli.*;
import org.jgroups.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.*;

/**
 * Created by VictoriatheEast on 5/28/17.
 */


public class Peer{

    //options - args
    private static final String PORT_ARG = "port";
    private static final String CONTINENT_ARG = "continent";
    private static final String COUNTRY_ARG = "country";
    private static final String MARKET_ARG = "market";
    private static final String QUANT_FILE_ARG = "qtyFile";
    private static final String PRICE_FILE_ARG = "priceFile";
    private static final String RECOVER_ARG = "recover";
    private static final String SUPER_ARG = "super";
    private static final String SUPER_PORT_ARG = "superPort";
    private static final String MY_IP = "127.0.0.1";

    private static final Logger logger = LoggerFactory.getLogger(Peer.class);
    private static DateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm");
    private static final ExecutorService pool = Executors.newFixedThreadPool(5);

    private static String ADMIN_IP = "127.0.0.1"; // yes, we would never do this if we weren't running just locally
    private static int ADMIN_PORT = 8090;
    private static List<PeerData> peerNetwork = Collections.emptyList();
    private static List<PeerData> superpeerNetwork = Collections.emptyList();

    //private PeerData mySuper = null;


    private int port;
    private int superPort;
    private String continent;
    private String country;
    private String market;
    private boolean isSuper;
    private Map<String, Stock> stockMap;

    public Peer(int port, String continent, String country, String market, boolean isSuper, int superPort){

        this.port = port;
        this.continent = continent;
        this.country = country;
        this.market = market;
        this.isSuper = isSuper;
        this.superPort = superPort;

    }


    public void registerWithSuperPeer(){
            try {
                ServerSocket serverSocket = new ServerSocket(superPort);
                while (true) {
                    Socket socket = serverSocket.accept();
                    Gson gson = new Gson();
                    BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
                    PeerRequest = null;
                    PeerRequest peerRequest = null;
                    PeerResponse peerResponse = null;
                    while (true) {
                        //Get request from trader
                        request = gson.fromJson(br.readLine(), TraderPeerRequest.class);

                        peerRequest = new PeerRequest(request.getDate(),request.getTrader(),request.getAction(),
                                request.getStock(),request.getShares());


                        //Process request
                        if(peerRequest.getAction() == TraderAction.CONSULT){
                            peerResponse = peer.consult(peerRequest);
                        } else {
                            peerResponse = peer.transact(peerRequest);
                        }

                        TraderPeerResponse traderResponse = new TraderPeerResponse(peerResponse.isSucceed(),
                                peerResponse.getAction(),peerResponse.getPrice());

                        //Send back response
                        pw.println(gson.toJson(traderResponse));

                    }
                    pool.submit(traderRequestHandler);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }



    }


    public Map<String, Stock> getStockMap(){
        return this.stockMap;
    }




    //Still working on
    public PeerResponse consult(PeerRequest request){
        TraderAction action = request.getAction();
        String stockName = request.getStock();
        if(action != null && action == TraderAction.CONSULT) {
            if (stockMap.containsKey(stockName)) {
                Stock stock = stockMap.get(stockName);
                double price = stock.getPrice();
                return new PeerResponse(true, action, price);
            } else {
                //Ask super peer
                //Todo

            }
        } else {
            return new PeerResponse();
        }
        return new PeerResponse();
    }

    //Still working on
    public PeerResponse transact(PeerRequest request) {
        TraderAction action = request.getAction();
        String stockName = request.getStock();
        int shares = request.getShares();

        return new PeerResponse();
        //Todo
    }



    public void start(){

        registerWithSuperPeer();


        ExecutorService pool = Executors.newCachedThreadPool();
        //Create thread with admin server
        //Create thread with super peer

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                Socket socket = serverSocket.accept();
                TraderRequestHandler traderRequestHandler = new TraderRequestHandler(socket,this);
                pool.submit(traderRequestHandler);
                checkSuperStatus(channel);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void checkSuperStatus(JChannel channel) {
        View view = channel.getView();
        Address address = view.getMembers().get(0);
        if (address.equals(channel.getAddress()) && !isSuper) { // implies a super has gone down and this peer is now top of list/super
            isSuper = true;
            requestGroupMembership(address);
            // TODO: Need to wait and then have the peer messaging update a class field of some sort that will always get sent in the admin client req
            waitFiveSecs();
            Socket adminClient = tryClient(ADMIN_IP, ADMIN_PORT);
            PeerAdminRequest request = new PeerAdminRequest(PeerAdminAction.REGISTER_NETWORK, this.continent, this.country, this.market,
                    this.peerNetwork, MY_IP, port);


        }
    }

    public static void waitFiveSecs() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException("Unable to sleep. Insomnia?", e);
        }
    }

    public void receive(Message msg) {
        try {
            PeerPeerMessage peerMessage = (PeerPeerMessage) msg.getObject();

            if (peerMessage.getAction() == PeerPeerAction.MEMBERSHIP_REQ) { // this means a new super wants to know we are alive
                PeerPeerMessage respMessage = new PeerPeerMessage(PeerPeerAction.MEMBERSHIP_RESP, null, MY_IP, port,
                        peerMessage.getSourceAddr(), peerMessage.getSourceIP(), peerMessage.getSourcePort(), market);
                Message response = new Message(peerMessage.getSourceAddr(), respMessage);
                channel.send(response);
            } else if (peerMessage.getAction() == PeerPeerAction.MEMBERSHIP_RESP && isSuper) {

            }
        } catch (Exception e) {
            throw new RuntimeException("Something went wrong sending message to peer", e);
        }
    }

    public static void requestGroupMembership(Address myAddress) {
        try {
            PeerPeerMessage peerMessage = new PeerPeerMessage(PeerPeerAction.MEMBERSHIP_REQ, myAddress, MY_IP, port, null, "", 0,
                    market);
            Message memberMessage = new Message(null, myAddress, peerMessage);
            channel.send(memberMessage);
        } catch (Exception e) {
            throw new RuntimeException("Something went wrong sending message out on channel", e);
        }
    }

    public static Socket tryClient(String ip, int port) {
        try {
            Socket client = new Socket(ip, port);
            return client;
        } catch (IOException e) {
            logger.error("Unable to secure connection at {} ip and {} port.", ip, port);
            throw new RuntimeException(e);
        }
    }

    public static AdminPeerResponse sendAdmin(Socket adminClient, PeerAdminRequest request) {
        try {
            Gson gson = new Gson();
            PrintWriter output = new PrintWriter(adminClient.getOutputStream(), true);
            output.println(gson.toJson(request));
        } catch (IOException e) {
            throw new RuntimeException("Error sending response to trader", e);
        }
    }

    public static void main(String[] args){
        Map<String, String> peerOpts = loadPeerOpts(args);
        Peer peer = new Peer(Integer.parseInt(peerOpts.get(PORT_ARG)), peerOpts.get(CONTINENT_ARG), peerOpts.get(COUNTRY_ARG),
                peerOpts.get(MARKET_ARG), Boolean.parseBoolean(peerOpts.get(SUPER_ARG)), Integer.parseInt(peerOpts.get(SUPER_PORT_ARG)));
        peer.start();
    }

    private static Map<String, String> loadPeerOpts(String[] args) {
        Options options = new Options();
        Option peerOpt = new Option("p", PORT_ARG, true, "Port for peer");
        Option contOpt = new Option("ct", CONTINENT_ARG, true, "Continent for peer");
        Option ctryOpt = new Option("cy", COUNTRY_ARG, true, "Country for peer");
        Option marketOpt = new Option("m", MARKET_ARG, true, "Market for peer");
        Option qtyOpt = new Option("q", QUANT_FILE_ARG, true, "Quantity file for peer");
        Option priceOpt = new Option("pr", PRICE_FILE_ARG, true, "Price file for peer");
        Option recOpt = new Option("r", RECOVER_ARG, true, "Recover boolean for peer");
        Option superOpt = new Option("s", SUPER_ARG, true, "Super boolean for peer");
        Option spPortOpt = new Option("sp", SUPER_PORT_ARG, true, "Super port for peer");
        peerOpt.setRequired(true);
        contOpt.setRequired(true);
        ctryOpt.setRequired(true);
        marketOpt.setRequired(true);
        qtyOpt.setRequired(true);
        priceOpt.setRequired(true);
        recOpt.setRequired(true);
        superOpt.setRequired(true);
        spPortOpt.setRequired(false);
        options.addOption(peerOpt);
        options.addOption(contOpt);
        options.addOption(ctryOpt);
        options.addOption(marketOpt);
        options.addOption(qtyOpt);
        options.addOption(priceOpt);
        options.addOption(recOpt);
        options.addOption(superOpt);
        options.addOption(spPortOpt);
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
            Map<String, String> args = new HashMap<>();
            String port = cmd.getOptionValue(PORT_ARG);
            String cont = cmd.getOptionValue(CONTINENT_ARG);
            String ctry = cmd.getOptionValue(COUNTRY_ARG);
            String market = cmd.getOptionValue(MARKET_ARG);
            String qtyFile = cmd.getOptionValue(QUANT_FILE_ARG);
            String priceFile = cmd.getOptionValue(PRICE_FILE_ARG);
            String recover = cmd.getOptionValue(RECOVER_ARG);
            String isSuper = cmd.getOptionValue(SUPER_ARG);
            String superPort = cmd.getOptionValue(SUPER_PORT_ARG);
            args.put(PORT_ARG, port);
            args.put(CONTINENT_ARG, cont);
            args.put(COUNTRY_ARG, ctry);
            args.put(MARKET_ARG, market);
            args.put(QUANT_FILE_ARG, qtyFile);
            args.put(PRICE_FILE_ARG, priceFile);
            args.put(RECOVER_ARG, recover);
            args.put(SUPER_ARG, isSuper);
            args.put(SUPER_PORT_ARG, superPort);
            return args;
        } catch (ParseException e) {
            formatter.printHelp("admin server help", options);
            throw new RuntimeException("Unable to read arguments, see help.");
        }
    }

}
