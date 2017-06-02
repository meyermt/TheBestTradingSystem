package com.vam.json;

/**
 * Created by VictoriatheEast on 6/2/17.
 */

import java.io.Serializable;
import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;

public class Issue {

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

    public void setQuantity(int quantity){
        this.quantity = quantity;
    }

}
