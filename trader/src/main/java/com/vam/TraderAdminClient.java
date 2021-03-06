package com.vam;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vam.json.AdminTraderResponse;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by ana_b on 6/3/2017.
 */
public class TraderAdminClient {

    private InetAddress mHostName;
    private final int mPortNumber;
    private  InetAddress msourceHost;
    private final int mSourcePort;

    public TraderAdminClient(String hostName, int portNumber,Object request,String sourceName, int sourcePortNumber) {

        try {
            this.mHostName = InetAddress.getLocalHost();
            this.msourceHost = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            System.out.println("Cannot obtain LocalHost");
            e.printStackTrace();
        }
        this.mPortNumber= portNumber;

        this.mSourcePort= sourcePortNumber;
        createSocketAndSend(request);
    }

    public void createSocketAndSend(Object serialize){

        try(
                Socket traderClientSocket = new Socket(mHostName, mPortNumber,msourceHost,mSourcePort);

                DataOutputStream out = new DataOutputStream(traderClientSocket.getOutputStream());

                BufferedReader in = new BufferedReader(new InputStreamReader(traderClientSocket.getInputStream()))
                ) {
            //Send request
            Gson gson = new Gson();
            out.writeBytes(gson.toJson(serialize));

            String fromAdminServer;

            //Receive response
            while ((fromAdminServer=in.readLine())!=null){

                //Read the server response and attempt to parse it as JSON
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson2 = gsonBuilder.create();
                AdminTraderResponse response = gson2.fromJson(in,AdminTraderResponse.class);
                break;

            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
