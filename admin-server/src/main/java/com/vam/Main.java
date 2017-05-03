package com.vam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by michaelmeyer on 5/1/17.
 */
public class Main {

    private static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        System.out.println("Hey you're running the admin server!");
        logger.info("You should see me log for admin server!");
    }

}
