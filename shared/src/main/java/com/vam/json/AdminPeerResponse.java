package com.vam.json;

import java.util.List;

/**
 * Created by michaelmeyer on 5/31/17.
 */
public class AdminPeerResponse {

    private AdminPeerResponseCode code;
    private List<PeerData> superpeers;

    public AdminPeerResponse() {}

    public AdminPeerResponse(AdminPeerResponseCode code, List<PeerData> superpeers) {
        this.code = code;
        this.superpeers = superpeers;
    }

    public AdminPeerResponseCode getCode() {
        return code;
    }

    public List<PeerData> getSuperpeers() {
        return superpeers;
    }
}
