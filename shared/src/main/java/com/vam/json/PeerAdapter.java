package com.vam.json;

import org.jgroups.JChannel;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.blocks.RpcDispatcher;
import org.jgroups.blocks.MethodCall;

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


public class PeerAdapter extends ReceiverAdapter{

    //Todo recovery

    private static final Logger logger = LoggerFactory.getLogger(PeerAdapter.class);
    private static DateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm");

    protected int port;
    protected String continent;
    protected String market;
    protected boolean isSuper;
    protected String qtyFile;
    protected String priceFile;
    protected ConcurrentHashMap<String, Stock> stockMap = new ConcurrentHashMap<>();


    //Concurrent hash set managing its super peer and peers in the network
    protected ConcurrentHashMap.KeySetView<Address, Boolean> peerMap = ConcurrentHashMap.newKeySet();
    protected ConcurrentHashMap.KeySetView<Address, Boolean> superPeerMap = ConcurrentHashMap.newKeySet();


    protected JChannel channel;
    protected View view;
    protected RpcDispatcher rpcDispatcher;


    public PeerAdapter(int port, String continent, String market,
                       String qtyFile, String priceFile){

        this.port = port;
        this.continent = continent;
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


    public String getContinent(){
        return this.continent;
    }

    //The peer can get to know if a super peer joins the network coz we only have five, we can configure their name
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
                } else if(peerMap.contains(address)){
                    deletePeerAdmin();
                    peerMap.remove(address);
                    System.out.println(address + "left" + this.continent);

                }

            }
        }

        if(membersJoining != null){
            for(Address address : membersJoining){
                //If the peer is a super peer, the super peer is added
                //Hardcode a list of super peer logical name
                if(this.channel.getName(address).equals("Continent-SuperPeerAdapter")){
                    superPeerMap.add(address);
                } else {
                    peerMap.add(address);
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

    //Todo
    public void deletePeerAdmin(){

    }
    public AdminPeerResponse registerWithAdmin(){
        return new AdminPeerResponse();
    }

    public AdminPeerResponse lookForSuperPeer(){
        return new AdminPeerResponse();
    }

    private void registerWithSuperPeer(){
        MethodCall methodCall = new MethodCall("registerWithSuperPeer", new Object[]{this.market,this.channel.getAddress(),
        this.stockMap},new Class[]{String.class,Address.class,ConcurrentHashMap.class});
        String message = "Fails to register with super peer";

        PeerResponse response = null;
        if(!superPeerMap.isEmpty()){
            response = ResponseProcessor.rpcAll(this.rpcDispatcher,superPeerMap,methodCall,message);

        } else {
            throw new RuntimeException("Super peer is not in the network");
        }

        if(!response.isSucceed()){
            throw new RuntimeException(message);

        }

    }


    public PeerResponse consult(PeerRequest request){
        TraderAction action = request.getAction();
        String stockName = request.getStock();
        if(action != null && action == TraderAction.CONSULT) {
            if (stockMap.containsKey(stockName)) {
                Stock stock = stockMap.get(stockName);
                double price = stock.getPrice();
                String message = request.getStock() + " Price: " + price;
                return new PeerResponse(true, action, price,message);
            } else {
                //Ask super peer
                MethodCall methodCall = new MethodCall("consult", new Object[]{request},
                        new Class[]{PeerRequest.class});
                String error = "Fail to call consult the price of " + stockName + " in the exchange.";
                return ResponseProcessor.rpcConsult(rpcDispatcher, superPeerMap.iterator().next(), methodCall, error);


            }
        } else {
            String messge = "Fail to process consult the price";
            return new PeerResponse(false,messge);
        }

    }


    public PeerResponse transact(PeerRequest request) {
        TraderAction action = request.getAction();
        String stockName = request.getStock();
        int sharesRequested = request.getShares();

        if (action != null && action == TraderAction.BUY) {
            if (stockMap.containsKey(stockName)) {
                Stock stock = stockMap.get(stockName);
                stock.updateShares(request.getDate());

                boolean enoughShares = stock.getShares() >= sharesRequested;

                if (enoughShares) {
                    stock.setShares(stock.getShares() - sharesRequested);
                    String message = request.getDate() +
                            " Filled: User " + request.getTrader() + " Buy " + stockName + " " + sharesRequested + " " + stock.getPrice();
                    logger.info(message);
                    return new PeerResponse(true, request.getAction(), stock.getPrice(),message);
                }

                // fail to buy
                String message = null;
                if (!enoughShares) {
                    message = "does not have enough shares";
                }
                logger.info(message);
                return new PeerResponse(false, request.getAction(), stock.getPrice(), message);
            }
        } else if (action != null && action == TraderAction.SELL) {

            if (stockMap.containsKey(stockName)) {
                Stock stock = stockMap.get(stockName);
                stock.updateShares(request.getDate());
                stock.setShares(stock.getShares() + sharesRequested);

                String message = request.getDate() + " Filled: User " + request.getTrader() + " Sell " + stockName + " " + sharesRequested + " " + stock.getPrice();
                logger.info(message);
                return new PeerResponse(true, request.getAction(), stock.getPrice(),message);
            } else {
                //Buy stock remotely
                MethodCall methodCall = new MethodCall("transact", new Object[]{request},
                        new Class[]{PeerRequest.class});
                String error = "Fail to call transact remotely " + sharesRequested + " shares of " + stockName + " in the exchange.";
                return ResponseProcessor.rpcTransact(rpcDispatcher, superPeerMap.iterator().next(), methodCall, error);
            }

        } else {
            String message = action + " is not supported.";
            logger.error(message);
            return new PeerResponse(false, message);
        }

        return new PeerResponse(false, "Transact fails");


    }



    public void start(){
        //Look for super peer
        lookForSuperPeer();

        readQtyFile();
        readPriceFile();
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

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static void main(String[] args){
        String[] inputs = new String[5];
        //Parse command lines
        if (args.length == 5) {

            if (!args[0].contains("=") || !args[1].contains("=") || !args[2].contains("=")
                    || !args[3].contains("=") || !args[4].contains("=") ) {
                throw new IllegalArgumentException("Command Line Argument is in wrong format, " +
                        "java PeerAdapter --port=1000 --continent=america --market=new_york --qtyFile=qty_stocks.csv" +
                        "--priceFile=price_stocks.csv --recover=false");
            } else {

                String[] split1 = args[0].split("=");
                String command1 = split1[0];
                String[] split2 = args[1].split("=");
                String command2 = split2[0];
                String[] split3 = args[1].split("=");
                String command3 = split3[0];
                String[] split4 = args[0].split("=");
                String command4 = split4[0];
                String[] split5 = args[1].split("=");
                String command5 = split5[0];



                if (!command1.equals("--port") || !command2.equals("--continent") || !command3.equals("--market")
                        ||!command4.equals("--qtyFile") || !command5.equals("--priceFile") ) {

                    throw new IllegalArgumentException("Command Line Argument is in wrong format, " +
                            "java PeerAdapter --port=1000 --continent=america --market=new_york --qtyFile=qty_stocks.csv" +
                            "--priceFile=price_stocks.csv --recover=false");


                }

                inputs[0] = split1[1];
                inputs[1] = split2[1];
                inputs[2] = split3[1];
                inputs[3] = split4[1];
                inputs[4] = split5[1];

            }


        } else {

            throw new IllegalArgumentException("Command Line Argument is in wrong format, " +
                    "java PeerAdapter --port=1000 --continent=america --market=new_york --qtyFile=qty_stocks.csv" +
                    "--priceFile=price_stocks.csv --recover=false");

        }

        PeerAdapter peer = new PeerAdapter(Integer.parseInt(inputs[0]),inputs[1],inputs[2],inputs[3],inputs[4]);
        peer.start();

    }


}
