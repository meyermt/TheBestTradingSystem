package com.vam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.KeyListener;

/**
 * Created by michaelmeyer on 5/1/17.
 */
public class Main implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(Main.class);
    private Thread mRenderThread;

    public static void main(String[] args) {
        System.out.println("Hey you're running the trader!");
        logger.info("You should see me log for trader!");
        Main trader = new Main();
    }

    public Main() {
        TradeFrame frame = new TradeFrame(this);
    }


    @Override
    public void run() {
        Main trader = new Main();
        trader.startRendering();
    }

    public void startRendering() {

        if (this.mRenderThread == null) {
            //All threads that are created in java need to be passed a Runnable object.
            //In this case we are making the "Runnable Object" the actual game instance.
            this.mRenderThread = new Thread(this);
            //Start the thread
            this.mRenderThread.start();
        }
    }
}
