package com.vam.listener;

import com.vam.peer.Peer;

import java.net.ServerSocket;

/**
 * Created by michaelmeyer on 6/4/17.
 */
public class PeerListener implements Runnable {

    private Peer peer;
    private ServerSocket peerServerSocket;

    public PeerListener(Peer peer, ServerSocket peerServerSocket) {
        this.peer = peer;
        this.peerServerSocket = peerServerSocket;
    }


    @Override
    public void run() {

    }
}
