package com.vam.dao;

/**
 * Created by michaelmeyer on 6/3/17.
 */
public interface MarketDAO {

    double getPrice(String stock);
    void insertPrice(String stock);
    int getQuantity(String stock);
    void insertQuantity(String stock);

}
