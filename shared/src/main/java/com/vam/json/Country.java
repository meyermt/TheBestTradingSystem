package com.vam.json;

/**
 * Created by ana_b on 6/3/2017.
 */
public enum Country {

    France("France"), USA("USA"),Germany("Germany"),UK("UK"),Japan("Japan"),China("China"),Belgium("Belgium"),
    Portugal("Portugal"),Canada("Canada"),India("India"),Switzerland("Switzerland"),Australia("Australia"),Korea("Korea"),
    SouthAfrica("South Africa"),Brazil("Brazil");

    private String name;

    Country(String numVal) {
        this.name = numVal;
    }

    public String getName() {
        return name;
    }
}