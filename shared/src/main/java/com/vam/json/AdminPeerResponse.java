package com.vam.json;

import java.util.List;

/**
 * Created by michaelmeyer on 5/31/17.
 */
public class AdminPeerResponse {

    private AdminPeerResponseCode code;
    private List<Peer> superpeers;

    public AdminPeerResponse() {}

    public AdminPeerResponse(AdminPeerResponseCode code, List<Peer> superpeers) {
        this.code = code;
        this.superpeers = superpeers;
    }

    public AdminPeerResponseCode getCode() {
        return code;
    }

    public List<Peer> getSuperpeers() {
        return superpeers;
    }
}
