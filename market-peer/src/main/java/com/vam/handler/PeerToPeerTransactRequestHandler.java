package com.vam.handler;

import com.google.gson.Gson;
import com.vam.json.PeerPeerRequest;
import com.vam.json.PeerPeerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

/**
 * Created by VictoriatheEast on 6/4/17.
 */
public class PeerToPeerTransactRequestHandler implements Runnable {

    private Socket socket;

    private Logger logger = LoggerFactory.getLogger(PeerToPeerRequestHandler.class);

    public PeerToPeerTransactRequestHandler(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run(){

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
            PeerPeerRequest peerPeerRequest = new PeerPeerRequest(request.getAction(),
                    0, stockName, 0.0);
            Gson gson = new Gson();
            pw.println(gson.toJson(peerPeerRequest));
            socket.setSoTimeout(1000);
            System.out.println("Timeout");
            PeerPeerResponse peerPeerResponse = gson.fromJson(br.readLine(), PeerPeerResponse.class);
            if (peerPeerResponse != null) {
                return peerPeerResponse;
            } else {
                return new PeerPeerResponse(false, request.getAction(), 0.0, stockName, 0);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
