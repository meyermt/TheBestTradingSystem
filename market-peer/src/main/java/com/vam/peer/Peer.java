package com.vam.peer;

import com.google.gson.Gson;
import com.vam.dao.MarketDAO;
import com.vam.dao.MarketDAOSQLLite;
//import com.vam.handler.TraderRequestHandler;
import com.vam.json.*;
import com.vam.listener.AdminListener;
import com.vam.listener.PeerListener;
import org.apache.commons.cli.*;

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
    private static final String TRADER_PORT_ARG = "traderPort";
    private static final String ADMIN_PORT_ARG = "adminPort";
    private static final String PEER_PORT_ARG = "peerPort";
    private static final String CONTINENT_ARG = "continent";
    private static final String COUNTRY_ARG = "country";
    private static final String MARKET_ARG = "market";
    private static final String QUANT_FILE_ARG = "qtyFile";
    private static final String PRICE_FILE_ARG = "priceFile";
    private static final String RECOVER_ARG = "recover";
    private static final String SUPER_ARG = "super";
    private static final String SUPER_PORT_ARG = "superPort";
    private static final String MY_IP = "127.0.0.1";
    private static final String QTY_CSV = "qty_stocks.csv";
    private static final String PRICE_CSV = "price_stocks.csv";
    private static final String LOCAL_HOST = "127.0.0.1";

    private static final Logger logger = LoggerFactory.getLogger(Peer.class);
    private static DateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm");
    private static final ExecutorService pool = Executors.newCachedThreadPool();

    private static String ADMIN_IP = "127.0.0.1"; // yes, we would never do this if we weren't running just locally
    private static int ADMIN_TARGET_PORT = 8090;
    private static List<PeerData> peerNetwork = new ArrayList<>();
    private static List<PeerData> superpeerNetwork = new ArrayList<>();

    private int traderPort;
    private int peerPort;
    private int superPort;
    private int adminPort; // this is the admin port to LISTEN on
    private String continent;
    private String country;
    private String market;
    private boolean isSuper;
    private MarketDAO marketDAO;
    private Map<String, Integer> contMap;
    private int leftPort = 0;
    private int rightPort = 0;

    public Peer(int traderPort, int peerPort, String continent, String country, String market, boolean isSuper, int superPort, int adminPort){

        this.traderPort = traderPort;
        this.peerPort = peerPort;
        this.continent = continent;
        this.country = country;
        this.market = market;
        this.isSuper = isSuper;
        this.superPort = superPort;
        this.adminPort = adminPort;
        this.marketDAO = new MarketDAOSQLLite(QTY_CSV, PRICE_CSV, market);
    }

    public boolean getIsSuper() {
        return isSuper;
    }

    public String getContinent() {
        return continent;
    }

    public void processMarketAction(PeerToPeerMessage message) {

    }

    public void processMarketResponse(PeerToPeerMessage message) {

    }

    public void findMarketInNetworkSendAlong(PeerToPeerMessage message) {
        logger.info("Message for someone in my network");
        peerNetwork.stream()
                .forEach(peer -> {
                    if (message.getTargetMarket().equals(peer.getMarket())) {
                        passMessageToPeer(message, peer);
                    }
                });
    }

    public void superSendAlong(PeerToPeerMessage message) { // we know this is already not us
        logger.info("Message for another peer being sent");
        int targetInt = contMap.get(message.getTargetContinent());
        if (targetInt < contMap.get(continent)) {
            passMessageToSuper(message, leftPort);
        } else {
            passMessageToSuper(message, rightPort);
        }
    }

    private void passMessageToSuper(PeerToPeerMessage message, int port) {
        try {
            Socket peerClient = new Socket("127.0.0.1", port);
            Gson gson = new Gson();
            PrintWriter output = new PrintWriter(peerClient.getOutputStream(), true);
            output.println(gson.toJson(message));
            peerClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void passMessageToPeer(PeerToPeerMessage message, PeerData peer) {
        try {
            Socket peerClient = new Socket("127.0.0.1", peer.getPeerPort());
            Gson gson = new Gson();
            PrintWriter output = new PrintWriter(peerClient.getOutputStream(), true);
            output.println(gson.toJson(message));
            peerClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void registerWithSuperPeer(){
        logger.info("attempting to register with my super peer");
        try {
            Socket peerClient = new Socket("127.0.0.1", superPort);
            PeerData me = new PeerData(MY_IP, peerPort, traderPort, continent, country, market, false);
            PeerToPeerMessage request = new PeerToPeerMessage(PeerToPeerAction.JOIN_PEER_NETWORK, null, me, Collections.emptyList());
            Gson gson = new Gson();
            PrintWriter output = new PrintWriter(peerClient.getOutputStream(), true);
            output.println(gson.toJson(request));
            peerClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addPeerToNetwork(PeerData peer) {
        logger.info("looks like we have another peer in our network. yippee");
        this.peerNetwork.add(peer);
        registerNetworkWithAdminServer();
        sendUpdatedNetwork();
    }

    public void updateMyNetwork(List<PeerData> peers) {
        logger.info("I need to update my network. I hope everything is ok...");
        this.peerNetwork = peers;
    }

    public void registerNetworkWithAdminServer(){
        logger.info("registering my peer network with admin. The life of a super!");
        try {
            Socket adminClient = new Socket("127.0.0.1", ADMIN_TARGET_PORT);
            PeerAdminRequest adminRequest = new PeerAdminRequest(PeerAdminAction.REGISTER_NETWORK, continent, country, market,
                    peerNetwork, LOCAL_HOST, traderPort, peerPort);
            Gson gson = new Gson();
            PrintWriter output = new PrintWriter(adminClient.getOutputStream(), true);
            output.println(gson.toJson(adminRequest));
            adminClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendUpdatedNetwork() {
        logger.info("new peer group membership available. Shipping it to everyone");
        peerNetwork.stream()
                .forEach(peer -> {
                    try {
                        Socket peerClient = new Socket("127.0.0.1", peer.getPeerPort());
                        PeerToPeerMessage request = new PeerToPeerMessage(PeerToPeerAction.UPDATE_PEER_NETWORK, null,null, peerNetwork);
                        Gson gson = new Gson();
                        PrintWriter output = new PrintWriter(peerClient.getOutputStream(), true);
                        output.println(gson.toJson(request));
                        peerClient.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    public void setSuperpeerNetwork(List<PeerData> peers) {
        logger.info("I now know my super peers, i'll remember that");
        this.superpeerNetwork = peers;
    }

//    public TraderPeerResponse consult(TraderPeerRequest request){
//        TraderAction action = request.getAction();
//        String stockName = request.getStock().getStock();
//        String marketName = request.getStock().getMarket();
//        String continentName = request.getStock().getContinent();
//
//        if(action != null && action == TraderAction.CONSULT) {
//            if (this.market == marketName) {
//                double price = this.marketDAO.getPrice(stockName);
//                return new PeerPeerResponse(true, action, price, stockName);
//            } else {
//                if (!isSuper) {
//                    try {
//
//                        ServerSocket serverSocket = new ServerSocket(superPort);
//                        while (true) {
//                            Socket socket = serverSocket.accept();
//                            PeerToPeerRequestHandler peerSpRequestHandler = new PeerToPeerRequestHandler(socket);
//                            pool.submit(peerSpRequestHandler);
//
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//
//                    }
//                } else {
//                    if (continentName == this.continent) {
//                        for (PeerData peerData : peerNetwork) {
//                            if (peerData.getMarket() == marketName) {
//                                String ip = peerData.getIp();
//                                int port = peerData.getPort();
//                                try {
//                                    Socket socket = new Socket(ip, port);
//                                    BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                                    PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
//                                    PeerPeerRequest peerPeerRequest = new PeerPeerRequest(request.getAction(),
//                                            0, stockName, 0.0);
//                                    Gson gson = new Gson();
//                                    pw.println(gson.toJson(peerPeerRequest));
//                                    socket.setSoTimeout(1000);
//                                    System.out.println("Timeout");
//                                    PeerPeerResponse peerPeerResponse = gson.fromJson(br.readLine(), PeerPeerResponse.class);
//                                    if (peerPeerResponse != null) {
//                                        return peerPeerResponse;
//                                    } else {
//                                        return new PeerPeerResponse(false, request.getAction(), 0.0, stockName, 0);
//                                    }
//
//
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }
//                    } else {
//                        //Talk to super peer
//
//                    }
//
//                }
//
//            }
//        }
//    }
//
//    public PeerPeerResponse transact(TraderPeerRequest request) {
//        TraderAction action = request.getAction();
//        String stockName = request.getStock().getStock();
//        int shares = request.getShares();
//        if(action != null) {
//            if (!isSuper) {
//                if (action == TraderAction.BUY) {
//                    int quantity = this.marketDAO.getQuantity(stockName);
//                    if (quantity > shares) {
//                        this.marketDAO.updateQuantity(stockName, quantity - shares);
//                        return new PeerPeerResponse(true, request.getAction(), shares, stockName,request.getShares());
//                    } else {
//                        return new PeerPeerResponse(false, request.getAction(),shares,stockName,0);
//                    }
//                } else {
//                    int qty = this.marketDAO.getQuantity(stockName);
//                    this.marketDAO.updateQuantity(stockName,qty + shares);
//                    return new PeerPeerResponse(true,request.getAction(),shares,stockName,request.getShares());
//                }
//            }
//        }
//
//
//    }



    public void start(){

        try {
            ServerSocket adminSocket = new ServerSocket(adminPort);
            AdminListener adminListener = new AdminListener(this, adminSocket);
            new Thread(adminListener).start();

            ServerSocket peerSocket = new ServerSocket(peerPort);
            PeerListener peerListener = new PeerListener(this, peerSocket);
            new Thread(peerListener).start();

            this.contMap = mapContinent();
            if (!isSuper) {
                registerWithSuperPeer();
            } else {
                this.leftPort = contMap.get(continent + "-left");
                this.rightPort = contMap.get(continent + "-right");
                registerNetworkWithAdminServer();
            }



            ServerSocket serverSocket = new ServerSocket(traderPort);
            while (true) {
                Socket socket = serverSocket.accept();
                //TraderRequestHandler traderRequestHandler = new TraderRequestHandler(socket,this);
                //new Thread(traderRequestHandler).start();
                //pool.submit(traderRequestHandler);

            }
        } catch (IOException e) {
            e.printStackTrace();
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

    public static void main(String[] args){
        Map<String, String> peerOpts = loadPeerOpts(args);
        Peer peer = new Peer(Integer.parseInt(peerOpts.get(TRADER_PORT_ARG)), Integer.parseInt(peerOpts.get(PEER_PORT_ARG)),
                peerOpts.get(CONTINENT_ARG), peerOpts.get(COUNTRY_ARG),
                peerOpts.get(MARKET_ARG), Boolean.parseBoolean(peerOpts.get(SUPER_ARG)), Integer.parseInt(peerOpts.get(SUPER_PORT_ARG)),
                Integer.parseInt(peerOpts.get(ADMIN_PORT_ARG)));
        peer.start();
    }

    private static Map<String, String> loadPeerOpts(String[] args) {
        Options options = new Options();
        Option tpOpt = new Option("tp", TRADER_PORT_ARG, true, "Trader Port for peer");
        Option ppOpt = new Option("pp", PEER_PORT_ARG, true, "Peer Port for peer");
        Option apOpt = new Option("ap", ADMIN_PORT_ARG, true, "Admin Port for peer");
        Option contOpt = new Option("ct", CONTINENT_ARG, true, "Continent for peer");
        Option ctryOpt = new Option("cy", COUNTRY_ARG, true, "Country for peer");
        Option marketOpt = new Option("m", MARKET_ARG, true, "Market for peer");
        Option recOpt = new Option("r", RECOVER_ARG, true, "Recover boolean for peer");
        Option superOpt = new Option("s", SUPER_ARG, true, "Super boolean for peer");
        Option spOpt = new Option("sp", SUPER_PORT_ARG, true, "Super Port for peer");
        tpOpt.setRequired(true);
        contOpt.setRequired(true);
        ctryOpt.setRequired(true);
        marketOpt.setRequired(true);
        ppOpt.setRequired(true);
        apOpt.setRequired(true);
        recOpt.setRequired(true);
        superOpt.setRequired(true);
        spOpt.setRequired(true);
        options.addOption(tpOpt);
        options.addOption(contOpt);
        options.addOption(ctryOpt);
        options.addOption(marketOpt);
        options.addOption(ppOpt);
        options.addOption(recOpt);
        options.addOption(superOpt);
        options.addOption(spOpt);
        options.addOption(apOpt);
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
            Map<String, String> mapArgs = new HashMap<>();
            String traderPort = cmd.getOptionValue(TRADER_PORT_ARG);
            String peerPort = cmd.getOptionValue(PEER_PORT_ARG);
            String cont = cmd.getOptionValue(CONTINENT_ARG);
            String ctry = cmd.getOptionValue(COUNTRY_ARG);
            String market = cmd.getOptionValue(MARKET_ARG);
            String qtyFile = cmd.getOptionValue(QUANT_FILE_ARG);
            String priceFile = cmd.getOptionValue(PRICE_FILE_ARG);
            String recover = cmd.getOptionValue(RECOVER_ARG);
            String isSuper = cmd.getOptionValue(SUPER_ARG);
            String superPort = cmd.getOptionValue(SUPER_PORT_ARG);
            String adminPort = cmd.getOptionValue(ADMIN_PORT_ARG);
            mapArgs.put(TRADER_PORT_ARG, traderPort);
            mapArgs.put(PEER_PORT_ARG, peerPort);
            mapArgs.put(CONTINENT_ARG, cont);
            mapArgs.put(COUNTRY_ARG, ctry);
            mapArgs.put(MARKET_ARG, market);
            mapArgs.put(QUANT_FILE_ARG, qtyFile);
            mapArgs.put(PRICE_FILE_ARG, priceFile);
            mapArgs.put(RECOVER_ARG, recover);
            mapArgs.put(SUPER_ARG, isSuper);
            mapArgs.put(SUPER_PORT_ARG, superPort);
            mapArgs.put(ADMIN_PORT_ARG, adminPort);
            return mapArgs;
        } catch (ParseException e) {
            formatter.printHelp("admin server help", options);
            throw new RuntimeException("Unable to read arguments, see help.");
        }
    }

    private static Map<String, Integer> mapContinent() {
        Map<String, Integer> contMap = new HashMap<>();
        contMap.put("America-left", 0);
        contMap.put("America", 8091);
        contMap.put("America-right", 8092);
        contMap.put("Europe-left", 8091);
        contMap.put("Europe", 8092);
        contMap.put("Europe-right", 8093);
        contMap.put("Africa-left", 8092);
        contMap.put("Africa", 8093);
        contMap.put("Africa-right", 8094);
        contMap.put("Asia-left", 8093);
        contMap.put("Asia", 8094);
        contMap.put("Asia-right", Integer.MAX_VALUE);
        return contMap;
    }

}
