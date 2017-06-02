package com.vam.json;

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

    private static final Logger logger = LoggerFactory.getLogger(Peer.class);
    private static DateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm");

    protected int port;
    protected String continent;
    protected String market;
    protected boolean isSuper;
    protected String qtyFile;
    protected String priceFile;
    protected boolean recover = false;


    private ConcurrentHashMap.KeySetView<Address, Boolean> currentMembers = ConcurrentHashMap.newKeySet();
    private ConcurrentHashMap<String, Stock> stockMap = new ConcurrentHashMap<>();

    protected JChannel channel;
    protected View view;
    protected RpcDispatcher rpcDispatcher;

    private static Peer peer;

    public Peer(){}

    public Peer(int port, String continent, String market,
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
                if(currentMembers.contains(address)){
                    currentMembers.remove(address);
                }

            }
        }

        if(membersJoining != null){
            for(Address address : membersJoining){
                //If the peer is a super peer, the super peer add the member
                if(isSuper == true){
                    currentMembers.add(address);
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
        lookForSuperPeer();

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

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static void main(String[] args){
        String[] inputs = new String[6];
        //Parse command lines
        if (args.length == 6) {

            if (!args[0].contains("=") || !args[1].contains("=") || !args[2].contains("=")
                    || !args[3].contains("=") || !args[4].contains("=") || !args[5].contains("=")) {
                throw new IllegalArgumentException("Command Line Argument is in wrong format, " +
                        "java Peer --port=1000 --continent=america --market=new_york --qtyFile=qty_stocks.csv" +
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
                String[] split6= args[1].split("=");
                String command6 = split6[0];


                if (!command1.equals("--port") || !command2.equals("--continent") || !command3.equals("--market")
                        ||!command1.equals("--qtyFile") || !command2.equals("--priceFile") || !command3.equals("--recover") ) {

                    throw new IllegalArgumentException("Command Line Argument is in wrong format, " +
                            "java Peer --port=1000 --continent=america --market=new_york --qtyFile=qty_stocks.csv" +
                            "--priceFile=price_stocks.csv --recover=false");


                }

                inputs[0] = split1[1];
                inputs[1] = split2[1];
                inputs[2] = split3[1];
                inputs[3] = split4[1];
                inputs[4] = split5[1];
                inputs[5] = split6[1];

            }


        } else {

            throw new IllegalArgumentException("Command Line Argument is in wrong format, " +
                    "java Peer --port=1000 --continent=america --market=new_york --qtyFile=qty_stocks.csv" +
                    "--priceFile=price_stocks.csv --recover=false");

        }

        Peer peer = new Peer(Integer.parseInt(inputs[0]),inputs[1],inputs[2],inputs[3],inputs[4]);
        if(inputs[5].equals("true")){
            peer.setRecover(true);
        } else {
            peer.setRecover(false);
        }
        peer.start();

    }


}
