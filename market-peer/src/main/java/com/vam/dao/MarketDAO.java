package com.vam.dao;

import java.io.BufferedReader;

/**
 * Created by michaelmeyer on 6/3/17.
 */
public interface MarketDAO {

    double getPrice(String stock);
    void updatePrice(String stock, double price);
    int getQuantity(String stock);
    void updateQuantity(String stock, int quantity);

}
