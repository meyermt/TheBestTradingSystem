package com.vam.dao;

import com.vam.json.PeerData;

import java.util.List;

/**
 * Interface for peer data access object
 * Created by michaelmeyer on 5/13/17.
 */
public interface PeersDAO {

    void insertPeer(String ip, int peerPort, int traderPort, String continent, String country, String market, boolean isSuper);

    List<PeerData> getCountryPeers(String country);

    /**
     * gets all super peers in the entire db
     * @return list of all super peers
     */
    List<PeerData> getSuperPeers();
    void deleteMarketPeer(String market);
    void deleteContinentPeers(String continent);

}