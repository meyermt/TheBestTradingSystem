package com.vam;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vam.json.AdminTraderResponse;
import com.vam.json.TraderAdminRequest;
import com.vam.json.TraderPeerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by ana_b on 6/3/2017.
 */
public class TraderClient {

    private InetAddress mHostName;
    private final int mPortNumber;
    private  InetAddress msourceHost;
    private final int mSourcePort;
    private Object mResponse;
    private Logger logger = LoggerFactory.getLogger(TraderClient.class);

    public TraderClient(String hostName, int portNumber, Object request, String sourceName, int sourcePortNumber) {

        try {
            this.mHostName = InetAddress.getLocalHost();
            this.msourceHost = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            System.out.println("Cannot obtain LocalHost");
            e.printStackTrace();
        }
        this.mPortNumber= portNumber;
        this.mSourcePort= sourcePortNumber;
    }

    public void sendLoginRequest(TraderAdminRequest request){

        try(
                //Socket traderClientSocket = new Socket(mHostName, mPortNumber,msourceHost,mSourcePort);
                Socket traderClientSocket = new Socket(mHostName, mPortNumber);
                PrintWriter outprinter =new PrintWriter(traderClientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(traderClientSocket.getInputStream()))
                ) {
            //Send request
            Gson gson = new Gson();
            logger.info("sending: {}", request);
            String toSend = gson.toJson(request);
            outprinter.println(toSend);
            System.out.println("sent Gson as Json");
            traderClientSocket.close();
        } catch (UnknownHostException e) {
            throw new RuntimeException("bad login", e);
        } catch (IOException e) {
            throw new RuntimeException("bad login", e);
        }
    }

    public void sendPeerRequest(TraderPeerRequest request){

        try(
                Socket peerClientSocket = new Socket(mHostName, mPortNumber);
                PrintWriter outprinter =new PrintWriter(peerClientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(peerClientSocket.getInputStream()))
        ) {
            //Send request
            Gson gson = new Gson();
            logger.info("sending: {}", request);
            String toSend = gson.toJson(request);
            outprinter.println(toSend);
            System.out.println("sent Gson as Json");
            peerClientSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("bad peer request", e);
        }
    }
}
