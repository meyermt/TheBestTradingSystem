package com.vam.handler;

import com.google.gson.Gson;
import com.vam.json.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
/**
 * Created by VictoriatheEast on 6/3/17.
 */
public class AdminRequestHandler implements Runnable {

    private Socket socket;

    public AdminRequestHandler(Socket socket){
            this.socket = socket;
    }

    @Override

    public void run(){
        try {
            Gson gson = new Gson();
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);



            AdminPeerResponse response = null;





        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
