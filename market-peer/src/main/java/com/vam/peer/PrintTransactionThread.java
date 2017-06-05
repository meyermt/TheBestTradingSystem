package com.vam.peer;


import com.vam.json.Transaction;

import java.util.List;
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

        List<Transaction> transactionList = peer.getTransactionList();
        System.out.println("================Transaction Record - "+peer.getMarket() + "==================");
        for(Transaction transaction:transactionList){
            System.out.println(transaction.toString());
        }

    }
}
