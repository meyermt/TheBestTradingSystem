package com.vam.json;

/**
 * Created by VictoriatheEast on 6/2/17.
 */
import org.jgroups.Address;
import org.jgroups.blocks.MethodCall;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.jgroups.blocks.RpcDispatcher;
import org.jgroups.util.Rsp;
import org.jgroups.util.RspList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.*;

public class ResponseProcessor  {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    //Wait until get back all the responses
    private static RequestOptions options = new RequestOptions(ResponseMode.GET_ALL, 10 * 1000);

    public static PeerResponse rpcConsult(RpcDispatcher rpcDispatcher, Address address, MethodCall methodCall, String message){
        PeerResponse response = null;
        try{
            response = rpcDispatcher.callRemoteMethod(address,methodCall,options);

        } catch(Exception e){
            e.printStackTrace();
            logger.error(message);

        }

        if(response != null){
            return response;
        } else {
            logger.error(message);
            return new PeerResponse(false,message);

        }
    }

    public static PeerResponse rpcTransact(RpcDispatcher rpcDispatcher, Address address, MethodCall methodCall, String message){
        PeerResponse response = null;
        try{
            response = rpcDispatcher.callRemoteMethod(address,methodCall,options);

        } catch(Exception e){
            e.printStackTrace();
            logger.error(message);

        }

        if(response != null){
            return response;
        } else {
            logger.error(message);
            return new PeerResponse(false,message);

        }

    }


    //Call remote method on a cluster member
    public static PeerResponse rpcIndividual(RpcDispatcher rpcDispatcher, Address address, MethodCall methodCall, String message) {
        ArrayList<Address> addresses = new ArrayList<>();
        addresses.add(address);
        return rpcAll(rpcDispatcher, addresses, methodCall, message);
    }


    //Call remote methods on all the cluster members
    public static PeerResponse rpcAll(RpcDispatcher rpcDispatcher, Collection<Address> addresses, MethodCall methodCall, String message) {
        RspList<PeerResponse> rspList = null;
        try {
            rspList = rpcDispatcher.callRemoteMethods(addresses, methodCall, options);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(message);
            return new PeerResponse(false, message);
        }

        //Empty collection
        if (rspList != null && rspList.size() == 0) {
            logger.error(message + "The number of targeted addresses is 0");
            return new PeerResponse(false, message);
        }

        //Go through response from one to one
        boolean succeed = true;
        String error = "The target address might be suspected";
        Iterator<Rsp<PeerResponse>> iterator = rspList.iterator();
        while (iterator.hasNext()) {
            Rsp<PeerResponse> rsp = iterator.next();
            if (rsp.wasReceived() && !rsp.wasSuspected()) {
                succeed = succeed && rsp.getValue().isSucceed();
                error = rsp.getValue().getMessge();
            } else {
                logger.error(message + error);
                succeed = succeed && false;
            }
        }

        return new PeerResponse(succeed, message);
    }



}
