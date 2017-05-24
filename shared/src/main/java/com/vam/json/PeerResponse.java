package com.vam.json;

import java.util.List;

/**
 * Created by michaelmeyer on 5/23/17.
 */
public class PeerResponse {

    private PeerResponseCode code;
    private List<Peer> peers;
    private List<Peer> superpeers;

    public PeerResponse() {};

    public PeerResponse(PeerResponseCode code, List<Peer> peers, List<Peer> superpeers) {
        this.code = code;
        this.peers = peers;
        this.superpeers = superpeers;
    }

    public PeerResponseCode getCode() {
        return code;
    }

    public List<Peer> getPeers() {
        return peers;
    }

    public List<Peer> getSuperpeers() {
        return superpeers;
    }
}
