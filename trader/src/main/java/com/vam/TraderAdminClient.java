package com.vam;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by ana_b on 6/3/2017.
 */
public class TraderAdminClient {

    private final String mHostName;
    private final int mPortNumber;

    public TraderAdminClient(String hostName, int portNumber){
        this.mHostName = hostName;
        this.mPortNumber= portNumber;
    }

    public void createSocket(){
        try(
                Socket traderClientSocket = new Socket(mHostName, mPortNumber);

                DataOutputStream out = new DataOutputStream(traderClientSocket.getOutputStream());

                BufferedReader in = new BufferedReader(new InputStreamReader(traderClientSocket.getInputStream()))
                ) {
            String fromAdminServer;

            while ((fromAdminServer=in.readLine())!=null){

                if (fromAdminServer.equals("")){
                    break;
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
