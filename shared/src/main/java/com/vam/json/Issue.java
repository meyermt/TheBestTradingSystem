package com.vam.json;

import java.io.*;

//Using date format to parse the date from the csv file
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;


/**
 * Issue class deals with the datetime and quantity of the stock the company issues
 */
public class Issue implements Serializable {

    private Date date;
    private int quantity;

    public Issue(String date, DateFormat dateFormat, int quantity){
        try {
            this.date = dateFormat.parse(date);
            this.quantity = quantity;
        } catch (ParseException e){
            e.printStackTrace();
        }
    }

    public Date getDate(){
        return this.date;
    }

    public int getQuantity(){
        return this.quantity;
    }

    public void setDate(Date date){
        this.date = date;
    }

    public void setQuantity(int number){
        this.quantity = number;
    }



}
