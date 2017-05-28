package com.vam.json;

//Using date format to parse the date from the csv file
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.io.*;

/**
 * Price class deals with the price of the stock at a given time
 */
public class Price implements Serializable {

    private Date date;
    private double currentPrice;

    public Price(String date, DateFormat dateFormat, double current){
        try {
            this.date = dateFormat.parse(date);
            this.currentPrice = current;
        } catch (ParseException e){
            e.printStackTrace();
        }

    }

    public Date getDate(){
        return this.date;
    }

    public double getCurrentPrice(){
        return this.currentPrice;
    }

    public void setCurrentPrice(Date date, double current){
        this.date = date;
        this.currentPrice = current;

    }

}
