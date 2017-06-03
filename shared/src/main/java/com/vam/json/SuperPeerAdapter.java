package com.vam.json;

/**
 * Created by VictoriatheEast on 6/2/17.
 */

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.View;
import org.jgroups.blocks.RpcDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SuperPeerAdapter extends PeerAdapter {

    private static final String SUPERPEERCLUSTER = "SuperPeerCluster";

    private static final Logger logger = LoggerFactory.getLogger(SuperPeerAdapter.class);

    private ConcurrentHashMap<String, String> stockToPeerMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, ConcurrentHashMap<String, Stock>> stockMapRegistration = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Address> peerRegistration = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Address, String> addressRegistration = new ConcurrentHashMap<>();

    //Super PeerAdapter network
    private ConcurrentHashMap.KeySetView<Address, Boolean> superPeers = ConcurrentHashMap.newKeySet();

    private String superPeerChannelName = null;

    private JChannel superPeerChannel;
    private RpcDispatcher superPeerRpcDispatcher;
    private View superPeerView;


    private ExecutorService pool = Executors.newCachedThreadPool();

    public SuperPeerAdapter(int port, String market, String continent, boolean recover, String qtyFile, String priceFile)  {
        super(port,continent,market,qtyFile,priceFile);
        this.isSuper = true;

        superPeerChannelName = this.continent + "_SuperPeer";

        //Having cluster doing peer-to-peer network
        Cluster continentCluster = new Cluster(this);

        try {

            superPeerChannel = new JChannel(new File("main/resources/superPeer.xml"));
            superPeerChannel.setName(superPeerChannelName);
            superPeerChannel.setReceiver(this);
            superPeerChannel.setDiscardOwnMessages(true);
            superPeerChannel.connect(SUPERPEERCLUSTER);
            superPeerRpcDispatcher = new RpcDispatcher(superPeerChannel,this,this,this);

        } catch (Exception e){
            e.printStackTrace();
        }
    }


    //Super PeerAdapter network
    @Override
    public void viewAccepted(View view) {
        System.out.println(view);
        if (superPeerView == null) {
            superPeerView = view;
            return;
        }

        List<Address> membersLeaving = new ArrayList<>();
        List<Address> membersJoining = new ArrayList<>();

        if (superPeerView.size() > view.size()) {
            membersLeaving = View.leftMembers(superPeerView, view);
        } else if (superPeerView.size() < view.size()) {
            membersJoining= View.leftMembers(view, superPeerView);
        }

        if (membersLeaving != null) {
            for (Address address : membersLeaving) {
                if (this.superPeers.contains(address)) {
                    superPeers.remove(address);
                }
            }
        }


        this.superPeerView= view;
    }

    //PeerAdapter registration
    public PeerResponse registerStockMap(String peer, Address peerAddress, ConcurrentHashMap<String, Stock> stockMap){
        try {
            peerRegistration.put(peer, peerAddress);
            addressRegistration.put(peerAddress,peer);
            stockMapRegistration.put(peer, stockMap);

            for (String key : stockMap.keySet()) {
                stockToPeerMap.put(key, peer);
            }
            logger.info(peer + " joined the " + this.continent + " cluster.");
            return new PeerResponse(true);
        } catch (Exception e) {
            logger.error("Fail to registerStockMap with the peer.");
            return new PeerResponse(false);
        }

    }



    public ConcurrentHashMap<String, Address> getPeerRegistration() {
        return this.peerRegistration;
    }

    public ConcurrentHashMap<Address, String> getAddressRegistration(){
        return this.addressRegistration;
    }



    public PeerResponse lookForStockInContinent(String stockName) {
        PeerResponse response = new PeerResponse(false, "Stock is not in the continent.");

        if (stockToPeerMap.containsKey(stockName)) {
            String peer = stockToPeerMap.get(stockName);
            if (peerRegistration.containsKey(peer)) {
                response.setSucceed(true);
                response.setMessage("Stock is in the continent");
                response.setAddress(peerRegistration.get(peer));
                return response;
            }
        }

        return response;
    }


    public Address lookForStockInWorld(String stockName) {


    }

    public PeerResponse consult(PeerRequest request){
        return new PeerResponse();

    }
    public PeerResponse transact(PeerRequest request) {
        return new PeerResponse();

    }

    public PeerResponse updateStockMap(String peerName, Stock stock) {
        try {
            if (stockMapRegistration.containsKey(peerName)) {
                ConcurrentHashMap<String, Stock> stockMap = stockMapRegistration.get(peerName);
                stockMap.put(stock.getStock(), stock);
                return new PeerResponse(true);
            }
        } catch (Exception e) {
            logger.error("Fail to updateStockMap in the super peer.");
            return new PeerResponse(false);
        }

        return new PeerResponse(false);
    }


    public void start() {


    }

    public ExecutorService getPool() {
        return pool;
    }


    public static void main(String[] args){

        String[] inputs = new String[2];
        //Parse command lines
        if (args.length == 2) {

            if (!args[0].contains("=") || !args[1].contains("=")) {
                throw new IllegalArgumentException("Command Line Argument is in wrong format, " +
                        "java SuperPeerAdapter --continent=north_america --recover==false");
            } else {

                String[] split1 = args[0].split("=");
                String command1 = split1[0];
                String[] split2 = args[1].split("=");
                String command2 = split2[0];


                if (!command1.equals("--continent") || !command2.equals("--recover")) {

                    throw new IllegalArgumentException("Command Line Argument is in wrong format, " +
                            "java SuperPeerAdapter --continent=north_america --recover==false");


                }

                inputs[0] = split1[1];
                inputs[1] = split2[1];

            }


        } else {

            throw new IllegalArgumentException("Command Line Argument is in wrong format, " +
                    "java SuperPeerAdapter --continent=north_america --recover==false");

        }


        SuperPeerAdapter superPeer = new SuperPeerAdapter(inputs[0], inputs[1].equals("true"));
        superPeer.start();
    }


}
