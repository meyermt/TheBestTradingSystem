package com.vam.dao;

import com.vam.bean.Stock;

import java.util.List;

/**
 * Created by michaelmeyer on 5/13/17.
 */
public interface StocksDAO {

    void insertStock(String continent, String country, String market, String stock);
    List<Stock> getAllStocks();
}
