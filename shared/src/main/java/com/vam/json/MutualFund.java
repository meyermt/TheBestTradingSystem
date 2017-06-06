package com.vam.json;

import java.util.HashMap;
import java.util.Map;
/**
 * Created by ana_b on 6/4/2017.
 */
public class MutualFund{

    private String name;
    private String abbrev;
    private HashMap<String, Stock> composition;


    public String getName() {
        return name;
    }

    public String getAbbrev() {
        return abbrev;
    }

    public HashMap<String, Stock> getComposition() {
        return composition;
    }

    public MutualFund(String fundName) {
        HashMap<String, Stock> MFB1=new  HashMap<String ,Stock>();
        HashMap<String, Stock> MFE1=new  HashMap<String ,Stock>();;
        HashMap<String, Stock> MFD1=new  HashMap<String ,Stock>();;


        MFB1.put("Deutsche Bank",new Stock("Europe",Country.Germany.getName(),"Frankfurt","Deutsche Bank",20));
        MFB1.put("Credit Agricole",new Stock("Europe",Country.France.getName(),"Euronext Paris","Credit Agricole",20));
        MFB1.put("Societe Generale",new Stock("Europe",Country.France.getName(),"Euronext Paris","Societe Generale",10));
        MFB1.put("American Express",new Stock("America",Country.USA.getName(),"New York Stock Exchange","American Express",20));
        MFB1.put("Goldman Sachs",new Stock("America",Country.USA.getName(),"New York Stock Exchange","Goldman Sachs",10));
        MFB1.put("JPMorgan Chase",new Stock("America",Country.USA.getName(),"New York Stock Exchange","JPMorgan Chase",15));
        MFB1.put("Nomura Holdins",new Stock("Asia",Country.Japan.getName(),"Tokyo","Nomura Holdings",5));

        MFE1 = new HashMap<String ,Stock>();
        MFB1.put("Petrobras",new Stock("America",Country.Brazil.getName(),"Sao Paulo","Petrobras",15));
        MFB1.put("BP",new Stock("Europe",Country.UK.getName(),"London","BP",15));
        MFB1.put("Total",new Stock("Europe",Country.France.getName(),"Euronext Paris","Total",40));
        MFB1.put("ExxonMobil",new Stock("America",Country.USA.getName(),"New York Stock Exchange","ExxonMobil",30));

        MFD1 = new HashMap<String ,Stock>();
        MFB1.put("Swire Pacific Limited",new Stock("Asia",Country.China.getName(),"Hong Kong","Swire Pacific Limited",15));
        MFB1.put("Softbank",new Stock("Asia",Country.Japan.getName(),"Tokyo","Softbank",35));
        MFB1.put("Sky PLC",new Stock("Europe",Country.UK.getName(),"London","Sky PLC",40));
        MFB1.put("Deutsche Lufthansa",new Stock("Europe",Country.Germany.getName(),"Frankfurt","Deutsche Lufthansa",10));

        if (fundName.equals("Mutal_Fund_Banking_1")){
            this.name = "Mutal_Fund_Banking_1";
            this.composition=MFB1;
        }
        if (fundName.equals("Mutal_Fund_Energy_1")){
            this.name = "Mutal_Fund_Energy_1";
            this.composition=MFE1;
        }
        if (fundName.equals("Mutal_Fund_Diversified_1")){
            this.name = "Mutal_Fund_Diversified_1";
            this.composition=MFD1;
        }
        else{
        throw new IllegalArgumentException("No fund for this name");
        }

    }
    }
