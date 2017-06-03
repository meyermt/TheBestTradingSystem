package com.vam.peer;

import com.vam.handler.TraderRequestHandler;
import com.vam.json.*;
import org.apache.commons.cli.*;
import org.jgroups.JChannel;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.blocks.RpcDispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jgroups.Address;


import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.*;

/**
 * Created by VictoriatheEast on 5/28/17.
 */


public class Peer extends ReceiverAdapter{

    //options - args
    private static final String PORT_ARG = "port";
    private static final String CONTINENT_ARG = "continent";
    private static final String COUNTRY_ARG = "country";
    private static final String MARKET_ARG = "market";
    private static final String QUANT_FILE_ARG = "qtyFile";
    private static final String PRICE_FILE_ARG = "priceFile";
    private static final String RECOVER_ARG = "recover";

    private static final Logger logger = LoggerFactory.getLogger(Peer.class);
    private static DateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm");
    private static String ADMIN_IP = "127.0.0.1"; // yes, we would never do this if we weren't running just locally
    private static int ADMIN_PORT = 8090;

    protected int port;
    protected String continent;
    protected String country;
    protected String market;
    protected static boolean isSuper;
    protected String qtyFile;
    protected String priceFile;
    protected boolean recover = false;


    private ConcurrentHashMap.KeySetView<Address, Boolean> superPeerMap = ConcurrentHashMap.newKeySet();
    private ConcurrentHashMap<String, Stock> stockMap = new ConcurrentHashMap<>();

    protected JChannel channel;
    protected View view;
    protected RpcDispatcher rpcDispatcher;

    private static Peer peer;

    public Peer(){}

    public Peer(int port, String continent, String country, String market,
                String qtyFile, String priceFile){

        this.port = port;
        this.continent = continent;
        this.country = country;
        this.market = market;
        this.isSuper = false;
        this.qtyFile = qtyFile;
        this.priceFile = priceFile;

        try {
            String config = "main/resources/"+this.continent+".xml";
            this.channel = new JChannel(new File(config));
            this.channel.setName(this.market);
            this.channel.setReceiver(this);
            this.channel.setDiscardOwnMessages(true);
            this.channel.connect(this.continent);
            this.rpcDispatcher = new RpcDispatcher(this.channel,this,this,this);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setSuper(){
        this.isSuper = true;
    }

    public void setPort(String portNumber){
        this.port = Integer.parseInt(portNumber);
    }

    public void setRecover(boolean recover){
        this.recover = recover;
    }




    @Override
    public void viewAccepted(View newView){
        System.out.println(this.view);
        if(this.view == null){
            this.view = newView;
        }

        List<Address> membersLeaving = new ArrayList<>();
        List<Address> membersJoining = new ArrayList<>();

        if(this.view.size() > newView.size()){
            membersLeaving = View.leftMembers(this.view,newView);
        } else if(this.view.size() < newView.size()){
            membersJoining = View.leftMembers(newView,this.view);
        }

        if(membersLeaving != null){
            for(Address address : membersLeaving){
                if(superPeerMap.contains(address)){
                    superPeerMap.remove(address);
                }

            }
        }

        if(membersJoining != null){
            for(Address address : membersJoining){
                //If the peer is a super peer, the super peer is added
                //Hardcode a list of super peer logical name
                if(this.channel.getName(address).equals("Continent-SuperPeer")){
                    superPeerMap.add(address);
                }

            }
        }

        this.view = newView;

    }

    //Todo
    //Format the file for easier parsing
    private void readPriceFile() {
        if (this.priceFile == null) {
            logger.info("Price file not available");
            return;
        }

        Scanner sc = null;
        try {
            sc = new Scanner(new File(this.priceFile));

            while (sc.hasNextLine()) {
                String line = sc.nextLine().replaceAll("\\s+", "");
                // skip empty line
                if (line.equals("")) {
                    logger.info("Empty line, process to next line...");
                    continue;
                }

                String[] split = line.split(",");
                if (split.length == 4) {
                    // get date
                    String date = split[0] + " " + split[1];

                    Stock stock = stockMap.get(split[2]);
                    stock.setPrice(Double.parseDouble(split[3]));

                } else {
                    logger.info("Wrong format");
                    continue;
                }
            }

            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    //Todo
    //Format the file for easier parsing
    private void readQtyFile() {

        if(this.qtyFile == null){
            logger.error("Price File is not there");
            return;
        }

        Scanner sc = null;
        try{
            sc = new Scanner(new File(this.qtyFile));

            Map<Integer, String> transformMap = new HashMap<>();

            while (sc.hasNextLine()) {
                String line = sc.nextLine().replaceAll("\\s+", "");
                boolean firstLine = true;
                if (line.equals("")) {
                    continue;
                }

                String[] split = line.split(",");
                if (split.length > 3) {
                    //Get stock names
                    if (firstLine) {
                        for (int i = 3; i < split.length; i++) {
                            stockMap.put(split[i], new Stock(split[i]));
                            transformMap.put(i, split[i]);
                        }
                        firstLine = false;
                        continue;
                    }


                    //Set shares
                    for (int i = 3; i < split.length; i++) {
                        int numIssued = Integer.parseInt(split[i]);
                        String stockName = transformMap.get(i);
                        Stock stock = stockMap.get(stockName);
                        stock.setShares(numIssued);

                    }
                } else {
                    logger.info("File input format wrong");
                    continue;
                }
            }

            sc.close();

        } catch (FileNotFoundException e){
            System.out.println("Price File is not there");

        }

    }

    public void lookForSuperPeer(){
        //Todo
    }

    private void registerWithSuperPeer(){
        //Working on

    }


    public void recover(){
        //Todo

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
        //Look for super peer
        if (!isSuper) {
            lookForSuperPeer();
        }

        if(recover){
            recover();
        } else {
            readQtyFile();
            readPriceFile();
            registerWithSuperPeer();

        }

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
            Socket adminClient = tryClient(ADMIN_IP, ADMIN_PORT);
            PeerAdminRequest request = new PeerAdminRequest(PeerAdminAction.REGISTER_NETWORK, this.continent, );
        }
    }

    public static Socket tryClient(String ip, int port) {

    }

    public static AdminPeerResponse sendAdmin(Socket adminClient, PeerAdminRequest request) {

    }

    public static void main(String[] args){
//        String[] inputs = new String[6];
        //Parse command lines
//        if (args.length == 7) {
//
//            if (!args[0].contains("=") || !args[1].contains("=") || !args[2].contains("=")
//                    || !args[3].contains("=") || !args[4].contains("=") || !args[5].contains("=")
//                    || !args[6].contains("=")) {
//                throw new IllegalArgumentException("Command Line Argument is in wrong format, " +
//                        "java Peer --port=1000 --continent=america --country=USA --market=new_york --qtyFile=qty_stocks.csv" +
//                        "--priceFile=price_stocks.csv --recover=false");
//            } else {
//
//                String[] split1 = args[0].split("=");
//                String command1 = split1[0];
//                String[] split2 = args[1].split("=");
//                String command2 = split2[0];
//                String[] split3 = args[1].split("=");
//                String command3 = split3[0];
//                String[] split4 = args[0].split("=");
//                String command4 = split4[0];
//                String[] split5 = args[1].split("=");
//                String command5 = split5[0];
//                String[] split6= args[1].split("=");
//                String command6 = split6[0];
//                String[] split7= args[1].split("=");
//                String command7 = split7[0];
//
//
//                if (!command1.equals("--port") || !command2.equals("--continent") || !command3.equals("--market")
//                        ||!command1.equals("--qtyFile") || !command2.equals("--priceFile") || !command3.equals("--recover") ) {
//
//                    throw new IllegalArgumentException("Command Line Argument is in wrong format, " +
//                            "java Peer --port=1000 --continent=america --market=new_york --qtyFile=qty_stocks.csv" +
//                            "--priceFile=price_stocks.csv --recover=false");
//
//
//                }
//
//                inputs[0] = split1[1];
//                inputs[1] = split2[1];
//                inputs[2] = split3[1];
//                inputs[3] = split4[1];
//                inputs[4] = split5[1];
//                inputs[5] = split6[1];
//
//            }
//
//
//        } else {
//
//            throw new IllegalArgumentException("Command Line Argument is in wrong format, " +
//                    "java Peer --port=1000 --continent=america --market=new_york --qtyFile=qty_stocks.csv" +
//                    "--priceFile=price_stocks.csv --recover=false");
//
//        }
        Map<String, String> peerOpts = loadPeerOpts(args);
        Peer peer = new Peer(Integer.parseInt(peerOpts.get(PORT_ARG)), peerOpts.get(CONTINENT_ARG), peerOpts.get(COUNTRY_ARG),
                peerOpts.get(MARKET_ARG), peerOpts.get(QUANT_FILE_ARG), peerOpts.get(PRICE_FILE_ARG));
        if(Boolean.parseBoolean(peerOpts.get(RECOVER_ARG))){
            peer.setRecover(true);
        } else {
            peer.setRecover(false);
        }
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
        peerOpt.setRequired(true);
        contOpt.setRequired(true);
        ctryOpt.setRequired(true);
        marketOpt.setRequired(true);
        qtyOpt.setRequired(true);
        priceOpt.setRequired(true);
        recOpt.setRequired(true);
        options.addOption(peerOpt);
        options.addOption(contOpt);
        options.addOption(ctryOpt);
        options.addOption(marketOpt);
        options.addOption(qtyOpt);
        options.addOption(priceOpt);
        options.addOption(recOpt);
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
            args.put(PORT_ARG, port);
            args.put(CONTINENT_ARG, cont);
            args.put(COUNTRY_ARG, ctry);
            args.put(MARKET_ARG, market);
            args.put(QUANT_FILE_ARG, qtyFile);
            args.put(PRICE_FILE_ARG, priceFile);
            args.put(RECOVER_ARG, recover);
            return args;
        } catch (ParseException e) {
            formatter.printHelp("admin server help", options);
            throw new RuntimeException("Unable to read arguments, see help.");
        }
    }
//    private static final String PORT_ARG = "port";
//    private static final String CONTINENT_ARG = "continent";
//    private static final String COUNTRY_ARG = "country";
//    private static final String MARKET_ARG = "market";
//    private static final String QUANT_FILE_ARG = "qtyFile";
//    private static final String PRICE_FILE_ARG = "priceFile";
//    private static final String RECOVER_ARG = "recover";


}
