package com.vam.dao;

import com.vam.json.PeerAdapter;

import java.util.List;

/**
 * Created by michaelmeyer on 5/13/17.
 */
public interface PeersDAO {

    void insertPeer(String ip, int port, String continent, String country, String market, boolean isSuper);
    List<PeerAdapter> getAllPeers();
    List<PeerAdapter> getContinentPeers(String continent);
    List<PeerAdapter> getCountryPeers(String country);
    List<PeerAdapter> getSuperPeers();
    void deleteMarketPeer(String market);
    void deleteContinentPeers(String continent);

}
