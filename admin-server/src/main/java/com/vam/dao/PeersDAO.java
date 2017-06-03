package com.vam.dao;

import com.vam.json.PeerData;

import java.util.List;

/**
 * Interface for peer data access object
 * Created by michaelmeyer on 5/13/17.
 */
public interface PeersDAO {

    /**
     * Inserts a peer into the db
     * @param ip IP of peer to insert
     * @param port port of peer to insert
     * @param continent continent of peer
     * @param country country of peer
     * @param market market peer is in
     * @param isSuper whether or not peer is a super peer
     */
    void insertPeer(String ip, int port, String continent, String country, String market, boolean isSuper);

    /**
     * Gets all peers in the entire db
     * @return list of all peers
     */
    List<PeerData> getAllPeers();

    /**
     * get all peers in a continent
     * @param continent continent to get peers for
     * @return list of all peers in continent
     */
    List<PeerData> getContinentPeers(String continent);
    List<PeerData> getCountryPeers(String country);

    /**
     * gets all super peers in the entire db
     * @return list of all super peers
     */
    List<PeerData> getSuperPeers();
    void deleteMarketPeer(String market);
    void deleteContinentPeers(String continent);

}
