package com.vam.dao;

import com.vam.json.Stock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.jvm.hotspot.oops.Mark;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

/**
 * Created by michaelmeyer on 6/3/17.
 */
public class MarketDAOSQLLite implements MarketDAO {

    private Logger logger = LoggerFactory.getLogger(MarketDAOSQLLite.class);
    private static final String DB_NAME = "market";
    private static final String MARKET_DB_FILE = "market.db";
    private final File dbFile;
    private final File loadStocksFile;
    private final File loadPriceFile;

    public MarketDAOSQLLite(String qtyStocksCsv, String priceCsv) {
        dbFile = new File("./" + MARKET_DB_FILE);
        if (!dbFile.isFile()) { // need to create a new db
            initNewDBAndTable();
            File loadStocksFile = new File("./" + qtyStocksCsv);
            File loadPriceFile = new File("./" + priceCsv);
            if (loadStocksFile.isFile() && loadPriceFile.isFile()) {
                loadStocksInTable();
            } else {
                throw new RuntimeException("Unable to find stocks csv file.");
            }
        }
    }

    public void insertStock(String continent, String country, String market, String stock) {
        String sql = "INSERT INTO stocks(continent, country, market, stock) VALUES(?,?,?,?)";

        try (Connection conn = this.connect(DB_NAME);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, continent);
            pstmt.setString(2, country);
            pstmt.setString(3, market);
            pstmt.setString(4, stock);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to insert" + stock + " into stocks db.", e);
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

    private void loadStocksInTable() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(STOCKS_CSV));
            String[] rawContinents = reader.readLine().split(",");
            String[] rawCountries = reader.readLine().split(",");
            String[] rawMarkets = reader.readLine().split(",");
            String[] rawStocks = reader.readLine().split(",");
            insertStocks(continents, countries, markets, stocks);
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException("Unable to load stocks csv file into table");
        }
    }

    private void insertStocks(String[] continents, String[] countries, String[] markets, String[] stocks) {
        for (int i = 0; i < continents.length; i++) {
            insertStock(continents[i], countries[i], markets[i], stocks[i]);
        }
    }

    private void initNewDBAndTable() {
        String url = "jdbc:sqlite:./" + STOCKS_DB_FILE;
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

        String stocks = "CREATE TABLE IF NOT EXISTS " + DB_NAME + " (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	continent text NOT NULL,\n"
                + "	country text NOT NULL,\n"
                + " market text NOT NULL,\n"
                + " stock text NOT NULL\n"
                + ");";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(stocks);
            logger.info("Created stocks table.");
        } catch (SQLException e) {
            throw new RuntimeException("Unable to create new stocks table", e);
        }
    }
}

