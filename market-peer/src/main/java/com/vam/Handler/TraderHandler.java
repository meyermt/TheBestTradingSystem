package com.vam.Handler;

/**
 * Created by VictoriatheEast on 5/27/17.
 */

import com.vam.model.Peer;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.net.*;
import java.util.concurrent.ConcurrentHashMap;

public class TraderHandler implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(TraderHandler.class);

    private Socket socket;
    private Peer peer;
    private ConcurrentHashMap<String, Trader> traders;

    public TraderHandler(Socket socket, Peer peer){
        this.socket = socket;
        this.peer = peer;
    }

    @Override public void run(){

    }
}
