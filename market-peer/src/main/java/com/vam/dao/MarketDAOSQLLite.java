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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by michaelmeyer on 6/3/17.
 */
public class MarketDAOSQLLite implements MarketDAO {

    private Logger logger = LoggerFactory.getLogger(MarketDAOSQLLite.class);
    private static final String DB_NAME = "market";
    private static final String MARKET_DB_FILE = "market.db";
    private final File dbFile;
    private final String qtyStocksCsv;
    private final String priceCsv;
    private final String marketName;

    public MarketDAOSQLLite(String qtyStocksCsv, String priceCsv, String marketName) {
        this.qtyStocksCsv = qtyStocksCsv;
        this.priceCsv = priceCsv;
        this.marketName = marketName;
        dbFile = new File("./" + MARKET_DB_FILE);
        File loadStocksFile = new File("./" + qtyStocksCsv);
        File loadPriceFile = new File("./" + priceCsv);
        if (!dbFile.isFile()) { // need to create a new db
            initNewDBAndTable();
            if (loadStocksFile.isFile() && loadPriceFile.isFile()) {
                loadStocksInTable();
            } else {
                throw new RuntimeException("Unable to find stocks csv file.");
            }
        }
    }

    public void insertStock(String stock, double price, int quantity) {
        String sql = "INSERT INTO market(stock, price, qunatity) VALUES(?,?,?)";

        try (Connection conn = this.connect(DB_NAME);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, stock);
            pstmt.setDouble(2, price);
            pstmt.setInt(3, quantity);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to insert" + stock + " into market db.", e);
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

    @Override
    public double getPrice(String stock) {
        double price = 0;
        String sql = "SELECT price FROM " + DB_NAME +
                " WHERE stock = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt  = conn.prepareStatement(sql)){
            pstmt.setString(1,stock);
            ResultSet rs  = pstmt.executeQuery();
            price = rs.getDouble("price");
            return price;
        } catch (SQLException e) {
            throw new RuntimeException("Unable to retrieve price in stock db.", e);
        }
    }

    @Override
    public void updatePrice(String stock, double price) {
        String sql = "UPDATE market SET price = ? "
                + "WHERE stock = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the corresponding param
            pstmt.setDouble(1, price);
            pstmt.setString(2, stock);
            // update
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to retrieve stocks in stock db.", e);
        }
    }

    @Override
    public int getQuantity(String stock) {
        int quantity = 0;
        String sql = "SELECT quantity FROM " + DB_NAME +
                " WHERE stock = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt  = conn.prepareStatement(sql)){
            pstmt.setString(1,stock);
            ResultSet rs  = pstmt.executeQuery();
            quantity = rs.getInt("quantity");
            return quantity;
        } catch (SQLException e) {
            throw new RuntimeException("Unable to retrieve stocks in stock db.", e);
        }
    }

    @Override
    public void updateQuantity(String stock, int quantity) {
        String sql = "UPDATE market SET quantity = ? "
                + "WHERE stock = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the corresponding param
            pstmt.setInt(1, quantity);
            pstmt.setString(2, stock);
            // update
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to retrieve stocks in stock db.", e);
        }
    }

    private void loadStocksInTable() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(priceCsv));
            BufferedReader reader1 = new BufferedReader(new FileReader(qtyStocksCsv));
            reader.readLine().split(","); // read continent
            reader.readLine().split(","); // read country
            String[] rawMarkets = reader.readLine().split(",");
            String[] rawStocks = reader.readLine().split(",");
            String[] rawPriceOne = reader.readLine().split(",");
            reader1.readLine().split(","); // read continent
            reader1.readLine().split(","); // read country
            reader1.readLine().split(","); // read market
            reader1.readLine().split(","); // read stock
            String[] rawQuantity = reader1.readLine().split(","); // read qty
            for (int i = 0; i < rawMarkets.length; i++) {
                if (rawMarkets[i].equals(marketName)) {
                    insertStock(rawStocks[i], Double.parseDouble(rawPriceOne[i]), Integer.parseInt(rawQuantity[i]));
                }
            }
            reader.close();
            reader1.close();
        } catch (IOException e) {
            throw new RuntimeException("Unable to load csv files into table");
        }
    }

    private void initNewDBAndTable() {
        String url = "jdbc:sqlite:./" + MARKET_DB_FILE;
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
                + "	stock text NOT NULL,\n"
                + "	price double NOT NULL,\n"
                + " quantity integer NOT NULL\n"
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

