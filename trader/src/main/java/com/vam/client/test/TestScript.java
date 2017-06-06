package com.vam.client.test;

/**
 * Created by michaelmeyer on 6/5/17.
 */

import com.vam.TraderClient;
import com.vam.json.Country;
import com.vam.json.Stock;
import com.vam.json.TraderAction;
import com.vam.json.TraderPeerRequest;

public class TestScript {

    public static void main(String[] args) {

        testSale();
    }

    public static void testSale() {
        //Creates sequential ports
        int quantity = 2;
        double price = 21;
        //int numberOfClients = 1000;
        int numberOfClients = 10;

        while (true) {
            //for (int i = 13457; i < 13457 + numberOfClients; i++) {
            for (int i = 13457; i < 13457 + numberOfClients; i++) {
                try {
                    runTestSale(quantity, price, i);
                    Thread.sleep(1000);
                    runTestPurchase(quantity, price, i);
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException("interrupted thread sleep");
                }
            }
        }
    }
    //Within specific ports for traders, sends
    public static void runTestSale(int quantity, double price, int traderSellBuyPort) {
        Stock test = new Stock("America", Country.USA.getName(), "New York Stock Exchange", "Wal-Mart");
        TraderPeerRequest request = new TraderPeerRequest("127.0.0.1", traderSellBuyPort, TraderAction.SELL, test, test.getContinent(),
                test.getMarket(), quantity, price);
        TraderClient client = new TraderClient("127.0.0.1", 8051, request, "127.0.0.1", traderSellBuyPort);
        client.sendPeerRequest(request);
    }


    //Within specific ports for traders, sends
    public static void runTestPurchase(int quantity, double price, int traderSellBuyPort) {
        Stock test = new Stock("America", Country.USA.getName(), "New York Stock Exchange", "Wal-Mart");
        TraderPeerRequest request = new TraderPeerRequest("127.0.0.1", traderSellBuyPort, TraderAction.BUY, test, test.getContinent(),
                test.getMarket(), quantity, price);
        TraderClient client = new TraderClient("127.0.0.1", 8051, request, "127.0.0.1", traderSellBuyPort);
        client.sendPeerRequest(request);
    }
}
