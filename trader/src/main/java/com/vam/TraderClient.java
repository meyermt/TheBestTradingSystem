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
public class TraderClient {

    private InetAddress mHostName;
    private final int mPortNumber;
    private  InetAddress msourceHost;
    private final int mSourcePort;
    private Object mResponse;

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
        mResponse=createSocketAndSend(request);
    }

    public Object getmResponse() {
        return mResponse;
    }

    public AdminTraderResponse createSocketAndSend(Object serialize){

        try(
                Socket traderClientSocket = new Socket(mHostName, mPortNumber,msourceHost,mSourcePort);

                DataOutputStream out = new DataOutputStream(traderClientSocket.getOutputStream());

                BufferedReader in = new BufferedReader(new InputStreamReader(traderClientSocket.getInputStream()))
                ) {
            //Send request
            Gson gson = new Gson();
            out.writeBytes(gson.toJson(serialize));
            out.writeBytes("Bye");
            System.out.println("sent Gson as Json");
            String fromAdminServer;

            //Receive response
            while ((fromAdminServer=in.readLine())!=null){

                //Read the server response and attempt to parse it as JSON
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson2 = gsonBuilder.create();
                return gson2.fromJson(in,AdminTraderResponse.class);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
