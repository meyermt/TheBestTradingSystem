package com.vam.json;

/**
 * The stock class renders and updates the stock name, value, and the number of shares available
 */


import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class Stock {

    private String continent;
    private String country;
    private String market;
    private String stock;
    private double price = 0.0;
    private int shares = 0;
    private boolean isFund;
    //Each stock has a list of issues and a list of price updates
    private ArrayList<Issue> issues = new ArrayList<>();
    private ArrayList<Price> prices = new ArrayList<>();

    public Stock() {
    }

    public Stock(String stock) {
        this.stock = stock;
    }

    /**
     * Constructs a stock that is included in the admin server inventory
     *
     * @param continent
     * @param country
     * @param market
     * @param stock
     */
    public Stock(String continent, String country, String market, String stock,boolean fund) {
        this.continent = continent;
        this.country = country;
        this.market = market;
        this.stock = stock;
        this.isFund=fund;
    }

    public Stock(String continent, String country, String market, String stock) {
        this.continent = continent;
        this.country = country;
        this.market = market;
        this.stock = stock;
        this.isFund=false;
    }


    public Stock(String stock, double price) {
        this.stock = stock;
        this.price = price;
    }


    public Stock(String stock, double price, int shares) {
        this.stock = stock;
        this.price = price;
        this.shares = shares;
    }

    public void updatePrice(Date date) {
        Iterator<Price> iterator = prices.iterator();
        while (iterator.hasNext()) {
            Price p = iterator.next();
            if (p.getDate().compareTo(date) <= 0) {
                this.price = p.getCurrentPrice();
                iterator.remove();
            } else {
                break;
            }
        }
    }

    public void updateShares(Date date) {
        Iterator<Issue> iterator = issues.iterator();
        while (iterator.hasNext()) {
            Issue issue = iterator.next();
            if (issue.getDate().compareTo(date) <= 0) {
                this.shares += issue.getQuantity();
                iterator.remove();
            } else {
                break;
            }
        }


    }

    public String getContinent() {
        return this.continent;
    }

    public String getCountry() {
        return country;
    }


    public String getMarket() {
        return market;
    }


    public String getStock() {
        return stock;
    }


    public double getPrice() {
        return price;
    }


    public int getShares() {
        return shares;
    }

    public void setShares(int shares) {
        this.shares = shares;
    }

    public void setPrice(double price) {
        this.price = price;
    }


    public ArrayList<Issue> getIssues() {
        return issues;
    }


    public ArrayList<Price> getPrices() {
        return prices;
    }


    @Override
    public String toString() {
        return "Stock{" +
                "continent='" + continent + '\'' +
                ", country='" + country + '\'' +
                ", market='" + market + '\'' +
                ", stock='" + stock + '\'' +
                '}';
    }

}
