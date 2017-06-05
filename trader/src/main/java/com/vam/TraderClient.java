package com.vam;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vam.json.AdminTraderResponse;
import com.vam.json.TraderAdminRequest;
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
            //outprinter.close();
            //outprinter.flush();
            System.out.println("sent Gson as Json");
            String fromAdminServer;
            traderClientSocket.close();
            //StringBuilder inputBuilder = new StringBuilder();
            //Receive response
            //while ((fromAdminServer=in.readLine())!=null){
            //    inputBuilder.append(fromAdminServer);
            //}
            //Read the server response and attempt to parse it as JSON
            //traderClientSocket.close();
            //logger.info("you got this string: {}", inputBuilder.toString());
            //return gson.fromJson(inputBuilder.toString(),AdminTraderResponse.class);
        } catch (UnknownHostException e) {
            throw new RuntimeException("bad login", e);
        } catch (IOException e) {
            throw new RuntimeException("bad login", e);
        }
    }
}
