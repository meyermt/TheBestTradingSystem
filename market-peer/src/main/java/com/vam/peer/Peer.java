package com.vam.peer;

import com.google.gson.Gson;
import com.vam.dao.MarketDAO;
import com.vam.dao.MarketDAOSQLLite;
import com.vam.handler.PeerSpRequestHandler;
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
    private static final String QTY_CSV = "qty_stocks.csv";
    private static final String PRICE_CSV = "price_stocks.csv";

    private static final Logger logger = LoggerFactory.getLogger(Peer.class);
    private static DateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm");
    private static final ExecutorService pool = Executors.newCachedThreadPool();

    private static String ADMIN_IP = "127.0.0.1"; // yes, we would never do this if we weren't running just locally
    private static int ADMIN_PORT = 8090;
    private static List<PeerData> peerNetwork = Collections.emptyList();
    private static List<PeerData> superpeerNetwork = Collections.emptyList();

    private int traderPort;
    private int peerPort;
    private int superPort;
    private String continent;
    private String country;
    private String market;
    private boolean isSuper;
    private List<PeerData> peers = Collections.emptyList();
    private List<PeerData> superpeers = Collections.emptyList();
    private MarketDAO marketDAO;
    private Map<String, Integer> contMap;
    private int leftPort = 0;
    private int rightPort = 0;

    public Peer(int traderPort, int peerPort, String continent, String country, String market, boolean isSuper, int superPort){

        this.traderPort = traderPort;
        this.peerPort = peerPort;
        this.continent = continent;
        this.country = country;
        this.market = market;
        this.isSuper = isSuper;
        this.superPort = superPort;
        this.marketDAO = new MarketDAOSQLLite(QTY_CSV, PRICE_CSV, market);
    }


    public void registerWithSuperPeer(){
            try {
                ServerSocket serverSocket = new ServerSocket(superPort);
                while (true) {

                    Socket socket = serverSocket.accept();
                    socket.setSoTimeout(10000);

                    Gson gson = new Gson();
                    BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);

                    PeerSPRequest registration = new PeerSPRequest(this.market);
                    pw.println(gson.toJson(registration));
                    logger.info("Having trouble connecting to the super peer");
                    String lines = br.readLine();
                    PeerSPResponse response = gson.fromJson(lines,PeerSPResponse.class);
                    peerNetwork = (List)response.getPeerMap();


                }
            } catch (IOException e) {
                e.printStackTrace();
            }
<<<<<<< HEAD
    }

    public void registerWithAdminServer(){
        try {
            ServerSocket serverSocket = new ServerSocket(ADMIN_PORT);
            while (true) {

                Socket socket = serverSocket.accept();
                socket.setSoTimeout(10000);
=======


    }

                Gson gson = new Gson();
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
                PeerAdminRequest registration = new PeerAdminRequest(PeerAdminAction.REGISTER_NETWORK, continent,
                        country, market, peers, MY_IP, port);
                pw.println(gson.toJson(registration));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TraderPeerResponse consult(TraderPeerRequest request){
        TraderAction action = request.getAction();
        String stockName = request.getStock().getStock();
        String marketName = request.getStock().getMarket();
        String continentName = request.getStock().getContinent();

        if(action != null && action == TraderAction.CONSULT) {
            if (this.market == marketName) {
                double price = this.marketDAO.getPrice(stockName);
                return new PeerPeerResponse(true, action, price, stockName);
            } else {
                if (!isSuper) {
                    try {

                        ServerSocket serverSocket = new ServerSocket(superPort);
                        while (true) {
                            Socket socket = serverSocket.accept();
                            PeerSpRequestHandler peerSpRequestHandler = new PeerSpRequestHandler(socket);
                            pool.submit(peerSpRequestHandler);

                        }
                    } catch (IOException e) {
                        e.printStackTrace();

                    }
                } else {
                    if(continentName == this.continent){
                        for(PeerData peerData : peerNetwork){
                            if(peerData.getMarket() == marketName){
                                String ip = peerData.getIp();
                                int port = peerData.getPort();
                                try{
                                    Socket socket = new Socket(ip,port);
                                    BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                                    PrintWriter pw = new PrintWriter(socket.getOutputStream(),true);
                                    PeerPeerRequest peerPeerRequest = new PeerPeerRequest(request.getAction(),
                                            0,stockName,0.0);
                                    Gson gson = new Gson();
                                    pw.println(gson.toJson(peerPeerRequest));
                                    socket.setSoTimeout(1000);

                                    PeerPeerResponse peerPeerResponse = gson.fromJson(br.readLine(),PeerPeerResponse.class);
                                    if(peerPeerResponse != null) {
                                        return peerPeerResponse;
                                    } else {
                                        return new PeerPeerResponse(false,request.getAction(),0.0,stockName);
                                    }


                                } catch (IOException e){
                                    e.printStackTrace();
                                }
                            }
                        }
                    } else {
                        //Talk to super peer
                        
                    }

                }

            }





    }

    public PeerPeerResponse transact(TraderPeerRequest request) {
        TraderAction action = request.getAction();
        String stockName = request.getStock().getStock();
        int shares = request.getShares();
        if(action != null){
            if(action == TraderAction.BUY){

                int quantity = this.marketDAO.getQuantity(stockName);
                if(quantity > shares){
                    this.marketDAO.updateQuantity(stockName,quantity - shares);
                    return new PeerPeerResponse(true,request.getAction(),shares,stockName);
                }
            }
        }


    }



    public void start(){

        ServerSocket adminSocket = new ServerSocket()
        this.contMap = mapContinent();
        if (!isSuper) {
            registerWithSuperPeer();
        } else {
            this.leftPort = contMap.get(continent + "-left");
            this.rightPort = contMap.get(continent + "-right");
            registerWithAdminServer();
        }


        try {
            ServerSocket serverSocket = new ServerSocket(traderPort);
            while (true) {
                Socket socket = serverSocket.accept();
                TraderRequestHandler traderRequestHandler = new TraderRequestHandler(socket,this);
                new Thread(traderRequestHandler).start();
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

    private static Map<String, Integer> mapContinent() {
        Map<String, Integer> contMap = new HashMap<>();
        contMap.put("America-left", 0);
        contMap.put("America-right", 8092)
        contMap.put("Europe-left", 8091);
        contMap.put("Europe-right", 8093);
        contMap.put("Africa-left", 8093);
        contMap.put("Africa-right", 8094);
        contMap.put("Asia-left", 8093);
        contMap.put("Asia-right", 0);
        return contMap;
    }

}
