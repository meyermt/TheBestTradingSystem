package com.vam;

import com.vam.model.Trader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main Driver for Trader
 */
public class Main {

    private static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        String[] inputs = new String[3];
        //Parse command lines
        if (args.length == 2) {

            if (!args[0].contains("=") || !args[1].contains("=")) {
                throw new IllegalArgumentException("Command Line Argument is in wrong format, " +
                        "For login: java Main --adminIP=127.0.0.1 --adminPort=2000");
            } else {

                String[] split1 = args[0].split("=");
                String command1 = split1[0];
                String[] split2 = args[1].split("=");
                String command2 = split2[0];

                //For connecting with the admin server
                if (command1.equals("--adminIP") || command2.equals("--adminPort")) {

                    inputs[0] = split1[1];
                    inputs[1] = split2[1];


                 //For connecting with the peer
                } else if (command1.equals("--peerIP") || command2.equals("--peerPort")){
                    inputs[0] = split1[1];
                    inputs[1] = split2[1];
                } else {
                    throw new IllegalArgumentException("Command Line Argument is in wrong format, " +
                            "For login: java Main --adminIP=127.0.0.1 --adminPort=2000");
                }

            }



        } else if (args.length == 3){
            if (!args[0].contains("=") || !args[1].contains("=") || !args[2].contains("=")) {
                throw new IllegalArgumentException("Command Line Argument is in wrong format, " +
                        "For exchange: java Main --peerIP=127.0.0.1 --peerPort=3000 --actionList=trader1.csv");
            } else {

                String[] split1 = args[0].split("=");
                String command1 = split1[0];
                String[] split2 = args[1].split("=");
                String command2 = split2[0];

                //For connecting with the admin server
                if (command1.equals("--peerIP") || command2.equals("--peerPort")){
                    inputs[0] = split1[1];
                    inputs[1] = split2[1];
                } else {
                    throw new IllegalArgumentException("Command Line Argument is in wrong format, " +
                            "For exchange: java Main --peerIP=127.0.0.1 --peerPort=3000 --actionList=trader1.csv");
                }

            }

            Trader trader = new Trader(inputs[0],Integer.parseInt(inputs[1]),inputs[2]);
            trader.start();

        } else {
            throw new IllegalArgumentException
                    ("Command Line Argument is in wrong format, " +
                            "For login:  java Main --adminIP=127.0.0.1 --adminPort=2000" +
                            "For exchange: java Main --peerIP=127.0.0.1 --peerPort=3000 --actionList=trader1.csv");
        }


        }


    }

}
