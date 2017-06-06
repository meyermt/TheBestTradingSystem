package com.vam.peer;


import com.vam.json.Transaction;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by VictoriatheEast on 6/4/17.
 */
public class PrintTransactionThread implements Runnable{

    private Peer peer;

    public PrintTransactionThread(Peer peer){
        this.peer = peer;
    }

    @Override
    public void run(){

        try {
            Thread.sleep(10000);
            System.out.println("================Transaction Record - " + peer.getMarket() + "==================");
            for (Transaction transaction : peer.getTransactionSet()) {
                System.out.println(transaction.toString());
            }
        } catch (InterruptedException e){
            e.printStackTrace();
        }

    }
}
