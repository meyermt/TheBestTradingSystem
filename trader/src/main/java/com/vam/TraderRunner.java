//package com.vam;
//
//import com.vam.client.server.AdminListener;
//import com.vam.client.server.PeerListener;
//import com.vam.json.Stock;
//
//import java.net.ServerSocket;
//import java.util.Optional;
//
///**
// * Created by michaelmeyer on 6/5/17.
// */
//public class TraderRunner {
//
//    private TradePanel panel;
//    private int adminPort;
//    private int peerPort;
//
//    public TraderRunner(int adminPort, int peerPort) {
//        panel = new TradePanel();
//        this.adminPort = adminPort;
//        this.peerPort = peerPort;
//    }
//
//    public runTrader() {
//        ServerSocket adminSocket = new ServerSocket(adminPort);
//        AdminListener adminListener = new AdminListener(true, panel, Optional.of(this), adminSocket);
//        new Thread(adminListener).start();
//
//        ServerSocket peerSocket = new ServerSocket(peerPort);
//        PeerListener peerListener = new PeerListener(true, panel, Optional.of(this), peerSocket);
//        new Thread(peerListener).start();
//
//        Stock stock = new Stock("America", "USA", "New York Stock Exchange", "Exxonmobil", false);
//
//        try {
//
//        }
//    }
//}
