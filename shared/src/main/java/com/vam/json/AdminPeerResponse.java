package com.vam.json;

import java.util.List;

/**
 * Created by michaelmeyer on 5/31/17.
 */
public class AdminPeerResponse {

    private AdminPeerResponseCode code;
    private List<PeerAdapter> superpeers;

    public AdminPeerResponse() {}

    public AdminPeerResponse(AdminPeerResponseCode code, List<PeerAdapter> superpeers) {
        this.code = code;
        this.superpeers = superpeers;
    }

    public AdminPeerResponseCode getCode() {
        return code;
    }

    public List<PeerAdapter> getSuperpeers() {
        return superpeers;
    }
}
