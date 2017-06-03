package com.vam.dao;

import com.vam.json.PeerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by michaelmeyer on 5/13/17.
 */
public class PeersDAOSQLLite implements PeersDAO {

    private Logger logger = LoggerFactory.getLogger(PeersDAOSQLLite.class);
    private static final String DB_NAME = "peers";
    private static final String PEERS_DB_FILE = "peers.db";

    public PeersDAOSQLLite() {
        File db = new File("./" + PEERS_DB_FILE);
        if (!db.isFile()) { // need to create a new db
            initNewDBAndTable();
        }
    }

    public void insertPeer(String ip, int port, String continent, String country, String market, boolean isSuper) {
        String delSql = "DELETE FROM peers WHERE market = ?";
        String sql = "INSERT INTO peers(ip, port, continent, country, market, super) VALUES(?,?,?,?,?,?)";

        try (Connection conn = this.connect(DB_NAME);
            PreparedStatement pstmt = conn.prepareStatement(delSql)) {
            pstmt.setString(1, market);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to delete" + market + " from peers db.", e);
        }

        try (Connection conn = this.connect(DB_NAME);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ip);
            pstmt.setInt(2, port);
            pstmt.setString(3, continent);
            pstmt.setString(4, country);
            pstmt.setString(5, market);
            pstmt.setBoolean(6, isSuper);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to insert" + market + " into peer db.", e);
        }
    }

    public List<PeerAdapter> getAllPeers() {
        String sql = "SELECT id, ip, port, continent, country, market, super FROM " + DB_NAME;
        try (Connection conn = this.connect(DB_NAME);
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            return collectPeers(rs);
        } catch (SQLException e) {
            throw new RuntimeException("Unable to retrieve stocks in stock db.", e);
        }
    }

    public List<PeerAdapter> getSuperPeers() {
        String sql = "SELECT id, ip, port, continent, country, market, super FROM " + DB_NAME +
                " WHERE super = 1";
        try (Connection conn = this.connect(DB_NAME);
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            return collectPeers(rs);
        } catch (SQLException e) {
            throw new RuntimeException("Unable to retrieve stocks in stock db.", e);
        }
    }

    public List<PeerAdapter> getContinentPeers(String continent) {
        String sql = "SELECT id, ip, port, continent, country, market, super FROM " + DB_NAME +
                " WHERE continent = ?";
        return getPeerOneWhere(sql, continent);
    }

    public List<PeerAdapter> getCountryPeers(String country) {
        String sql = "SELECT id, ip, port, continent, country, market, super FROM " + DB_NAME +
                " WHERE country = ?";
        return getPeerOneWhere(sql, country);
    }

    private List<PeerAdapter> getPeerOneWhere(String sql, String clause) {
        try (Connection conn = this.connect(DB_NAME);
            PreparedStatement pstmt  = conn.prepareStatement(sql)){
            pstmt.setString(1, clause);
            ResultSet rs    = pstmt.executeQuery();

            return collectPeers(rs);
        } catch (SQLException e) {
            throw new RuntimeException("Unable to retrieve stocks in stock db.", e);
        }
    }

    public void deleteMarketPeer(String market) {
        String sql = "DELETE FROM " + DB_NAME + " WHERE market = ?";

        try (Connection conn = this.connect(DB_NAME);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, market);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to delete" + market + " from peers db.", e);
        }
    }

    public void deleteContinentPeers(String continent) {
        String sql = "DELETE FROM " + DB_NAME + " WHERE continent = ?";

        try (Connection conn = this.connect(DB_NAME);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, continent);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to delete" + continent + " from peers db.", e);
        }
    }

    private List<PeerAdapter> collectPeers(ResultSet rs) {
        try {
            List<PeerAdapter> peers = new ArrayList<>();
            while (rs.next()) {
                PeerAdapter peer = new PeerAdapter(rs.getString("ip"),
                        rs.getInt("port"), rs.getString("continent"),
                        rs.getString("country"), rs.getString("market"), rs.getBoolean("super"));
                peers.add(peer);
            }
            return peers;
        } catch (SQLException e) {
            throw new RuntimeException("Unable to retrieve peers in peer db.", e);
        }
    }

    private Connection connect(String dbName) {
        // SQLite connection string
        String url = "jdbc:sqlite:./" + dbName + ".db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            throw new RuntimeException("Unable to connect to sql db.", e);
        }
        return conn;
    }

    private void initNewDBAndTable() {
        String url = "jdbc:sqlite:./" + PEERS_DB_FILE;
        logger.info("No stocks db exists so creating one.");
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                logger.info("The driver name is " + meta.getDriverName());
                logger.info("New database has been created.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Unable to create new database and tables.", e);
        }

        String peers = "CREATE TABLE IF NOT EXISTS " + DB_NAME + " (\n"
                + "	id integer PRIMARY KEY,\n"
                + " ip text NOT NULL,\n"
                + " port integer NOT NULL,\n"
                + "	continent text NOT NULL,\n"
                + "	country text NOT NULL,\n"
                + " market text NOT NULL,\n"
                + " super boolean NOT NULL\n"
                + ");";


        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(peers);
            logger.info("Created peers table.");
        } catch (SQLException e) {
            throw new RuntimeException("Unable to create new peers table", e);
        }
    }
}
