package com.vam.clock;

import com.vam.dao.MarketDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by michaelmeyer on 6/4/17.
 */
public class DatabaseUpdater implements Runnable {

    private MarketDAO marketDAO;
    private String market;
    private String priceCsv;
    private Logger logger = LoggerFactory.getLogger(DatabaseUpdater.class);

    public DatabaseUpdater(MarketDAO marketDAO, String market, String priceCsv) {
        this.marketDAO = marketDAO;
        this.market = market;
        this.priceCsv = priceCsv;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(priceCsv));
            reader.readLine().split(","); // read continent
            reader.readLine().split(","); // read country
            String[] markets = reader.readLine().split(","); // read market
            String[] stocks = reader.readLine().split(","); // read stock
            String[] prices = reader.readLine().split(","); // read prices

            while (true) {
                for (int i = 0; i < markets.length; i++) {
                    if (markets[i].equals(market)) {
                        if (prices[i].equals("")) {
                            prices[i] = "0";
                        }
                        if (prices[i].equals("")) {
                            prices[i] = "0";
                        }
                        marketDAO.updatePrice(stocks[i], Double.parseDouble(prices[i]));
                    }
                }
                Thread.sleep(5000);
                prices = reader.readLine().split(","); // read new prices
            }
        } catch (IOException e) {
            throw new RuntimeException("could not read in new prices for market");
        } catch (InterruptedException e) {
            throw new RuntimeException(("your sleep was interrupted updater!"));
        }
    }
}
