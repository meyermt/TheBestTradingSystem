package com.vam;

import com.vam.gui.TradeFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * Created by michaelmeyer on 5/1/17.
 */
public class Main {

    private static Logger logger = LoggerFactory.getLogger(Main.class);
    /**
     * The constant SCREEN_WIDTH.
     */
    public static final int SCREEN_WIDTH = 800;
    /**
     * The constant SCREEN_HEIGHT.
     */
    public static final int SCREEN_HEIGHT = 400;

    public static void main(String[] args) {
        System.out.println("Hey you're running the trader!");
        logger.info("You should see me log for trader!");
        JFrame frame = new TradeFrame();
        frame.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        frame.setTitle("Financial Trader");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

}
