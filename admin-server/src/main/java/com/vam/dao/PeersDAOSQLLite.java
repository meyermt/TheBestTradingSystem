package com.vam.dao;

import com.vam.json.PeerData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of PeersDAO using SQLLite
 * Created by michaelmeyer on 5/13/17.
 */
public class PeersDAOSQLLite implements PeersDAO {

    private Logger logger = LoggerFactory.getLogger(PeersDAOSQLLite.class);
    private static final String DB_NAME = "peers";
    private static final String PEERS_DB_FILE = "peers.db";
    private static final String BACKUP_DIR = "backup";
    private static final String IP = "127.0.0.1";
    private final Path backupFile;
    private final File dbFile;

    /**
     * New Peers DB is created if it doesn't already exist
     */
    public PeersDAOSQLLite() {
        File backupDir = new File("backup");
        dbFile = new File("./" + PEERS_DB_FILE);
        backupFile = Paths.get(BACKUP_DIR, PEERS_DB_FILE);
        if (!dbFile.isFile()) { // need to create a new db
            initNewDBAndTable();
            initSPEntries();
        }
        // check if our backup dir exists or not
        if (!backupDir.isDirectory()) {
            logger.info("creating backup dir");
            backupDir.mkdir();
        }
    }

    public void initSPEntries() {
        // once distributed would need to update the IPs
        insertPeer(IP, 8091, 9091, "America", "USA", "New York Stock Exchange", true);
        insertPeer(IP, 8094, 9094, "Asia", "Japan", "Tokyo", true);
        insertPeer(IP, 8092, 9092, "Europe", "France", "Euronext Paris", true);
        insertPeer(IP, 8093, 9093, "Africa", "South Africa", "Johannesburg", true);
    }

    public void insertPeer(String ip, int peerPort, int traderPort, String continent, String country, String market, boolean isSuper) {
        String delSql = "DELETE FROM peers WHERE market = ?";
        String sql = "INSERT INTO peers(ip, peerPort, traderPort, continent, country, market, super) VALUES(?,?,?,?,?,?,?)";

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
            pstmt.setInt(2, peerPort);
            pstmt.setInt(3, traderPort);
            pstmt.setString(4, continent);
            pstmt.setString(5, country);
            pstmt.setString(6, market);
            pstmt.setBoolean(7, isSuper);
            pstmt.executeUpdate();
            logger.info("backing up peer database");
            Files.copy(dbFile.toPath(), backupFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (SQLException e) {
            throw new RuntimeException("Unable to insert" + market + " into peer db.", e);
        } catch (IOException e) {
            throw new RuntimeException("Unable to save backup copy of database after insertPeer", e);
        }
    }

    public List<PeerData> getSuperPeers() {
        String sql = "SELECT id, ip, peerPort, continent, country, market, super FROM " + DB_NAME +
                " WHERE super = 1";
        try (Connection conn = this.connect(DB_NAME);
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            return collectPeers(rs);
        } catch (SQLException e) {
            throw new RuntimeException("Unable to retrieve stocks in stock db.", e);
        }
    }

    /**
     *
     * @param country
     * @return
     */
    public List<PeerData> getCountryPeers(String country) {
        String sql = "SELECT id, ip, traderPort, continent, country, market, super FROM " + DB_NAME +
                " WHERE country = ?";
        return getPeerOneWhere(sql, country);
    }

    private List<PeerData> getPeerOneWhere(String sql, String clause) {
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
            logger.info("backing up peer database");
            Files.copy(dbFile.toPath(), backupFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (SQLException e) {
            throw new RuntimeException("Unable to delete" + market + " from peers db.", e);
        } catch (IOException e) {
            throw new RuntimeException("Unable to save backup copy of database after deleteMarketPeer", e);
        }
    }

    public void deleteContinentPeers(String continent) {
        String sql = "DELETE FROM " + DB_NAME + " WHERE continent = ?";

        try (Connection conn = this.connect(DB_NAME);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, continent);
            pstmt.executeUpdate();
            logger.info("backing up peer database");
            Files.copy(dbFile.toPath(), backupFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (SQLException e) {
            throw new RuntimeException("Unable to delete" + continent + " from peers db.", e);
        } catch (IOException e) {
            throw new RuntimeException("Unable to save backup copy of database after deleteContinentPeer", e);
        }
    }

    private List<PeerData> collectPeers(ResultSet rs) {
        try {
            List<PeerData> peers = new ArrayList<>();
            while (rs.next()) {
                PeerData peer = new PeerData(rs.getString("ip"),
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
                + " peerPort integer NOT NULL,\n"
                + " traderPort integer NOT NULL,\n"
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