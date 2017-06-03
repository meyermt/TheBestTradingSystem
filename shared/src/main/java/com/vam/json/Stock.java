package com.vam.json;

/**
 * Bean representing a stock in the trading system
 * Created by michaelmeyer on 5/13/17.
 */
public class Stock {

    private String continent;
    private String country;
    private String market;
    private String stock;

    public Stock() { }

    public Stock(String continent, String country, String market, String stock) {
        this.continent = continent;
        this.country = country;
        this.market = market;
        this.stock = stock;
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

    public String getContinent() {
        return continent;
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
}
