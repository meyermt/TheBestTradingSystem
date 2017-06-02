package com.vam.dao;

import com.vam.json.Peer;

import java.util.List;

/**
 * Created by michaelmeyer on 5/13/17.
 */
public interface PeersDAO {

    void insertPeer(String ip, int port, String continent, String country, String market, boolean isSuper);
    List<Peer> getAllPeers();
    List<Peer> getContinentPeers(String continent);
    List<Peer> getCountryPeers(String country);
    List<Peer> getSuperPeers();
    void deleteMarketPeer(String market);
    void deleteContinentPeers(String continent);

}
