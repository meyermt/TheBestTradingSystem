package com.vam.dao;

import com.vam.bean.Stock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by michaelmeyer on 5/13/17.
 */
public class StocksDAOSQLLite implements StocksDAO {

    private Logger logger = LoggerFactory.getLogger(StocksDAOSQLLite.class);
    private static final String DB_NAME = "stocks";
    private static final String STOCKS_DB_FILE = "stocks.db";
    private static final String STOCKS_CSV = "price_stocks.csv";

    public StocksDAOSQLLite() {
        File db = new File("./" + STOCKS_DB_FILE);
        if (!db.isFile()) { // need to create a new db
            initNewDBAndTable();
            File loadFile = new File("./" + STOCKS_CSV);
            if (loadFile.isFile()) {
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

    public List<Stock> getAllStocks() {
        String sql = "SELECT id, continent, country, market, stock FROM " + DB_NAME;
        List<Stock> stocks = new ArrayList<>();

        try (Connection conn = this.connect(DB_NAME);
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            while (rs.next()) {
                Stock stock = new Stock(rs.getString("continent"),
                        rs.getString("country"), rs.getString("market"),
                        rs.getString("stock"));
                stocks.add(stock);
            }
            return stocks;
        } catch (SQLException e) {
            throw new RuntimeException("Unable to retrieve stocks in stock db.", e);
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
            String[] continents = reader.readLine().split(",");
            String[] countries = reader.readLine().split(",");
            String[] markets = reader.readLine().split(",");
            String[] stocks = reader.readLine().split(",");
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
