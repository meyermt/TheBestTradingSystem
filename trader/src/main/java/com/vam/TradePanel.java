package com.vam;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ana_b on 5/12/2017.
 */
public class TradePanel extends JPanel{

    /**
     * The current state of the Trader (Login, Main Menu, Logout)
     */
    private int ScreenState=0;
    private String mUsername;
    private String mPassword;
    private Map<String,String> mStock;
    private int width=TradeFrame.FRAME_DIM.width;
    private int height=TradeFrame.FRAME_DIM.height;

    public TradePanel() {

        this.setPreferredSize(TradeFrame.FRAME_DIM);
        this.mStock=new HashMap<String,String>();
        ScreenState=0;
        //mPanel = new JPanel();
        //mPanel.setLayout(new BorderLayout());
    }

    public void setState(int tradeState) {
        ScreenState = tradeState;
    }

    @Override
    public void paintComponent(Graphics g) {
        // Call the super paintComponent of the panel
        super.paintComponent(g);

        /*g.setColor(Color.black);
        g.fillRect(0, 0, TradeFrame.FRAME_DIM.width, TradeFrame.FRAME_DIM.height);
        g.setColor(Color.WHITE);
        g.setFont(new Font("ARIAL", Font.BOLD, 16));
*/
        //Login
        if(ScreenState==0) {
            JLabel usernameLabel = new JLabel("Username:");
            JLabel passwordLabel = new JLabel("Password:");
            JTextField username= new JTextField (15);
            JTextField password= new JTextField(15);
            JButton log = new JButton("Login");
            mUsername=username.getText();
            mPassword=password.getText();

            add(usernameLabel, BorderLayout.CENTER);
            add(username, BorderLayout.CENTER);
            add(passwordLabel, BorderLayout.CENTER);
            add(password, BorderLayout.CENTER);
            add(log, BorderLayout.CENTER);

/*
            if (isLoginValid()){
                ScreenState=1;
            }
            else{
                JLabel problem = new JLabel("There's a problem with your login");
            }
*/
        }

        if (ScreenState == 1) {

            //Draw the main menu
            JLabel usernameLabel = new JLabel("Hello "+mUsername);
            JButton consPrice = new JButton("Estimate");
            JButton sell = new JButton("Sell");
            JButton buy = new JButton("Buy");
            JLabel stock = new JLabel("Stock:");
            JTextField stock_value= new JTextField (15);
            JLabel price = new JLabel("Price:");
            JTextField price_value= new JTextField (15);
            JLabel quantity = new JLabel("Quantity");
            JTextField quantity_value= new JTextField (15);
            int rows = mStock.size();
            JTextArea availableStocks = new JTextArea(rows,1);
            for (String stockName : mStock.values()) {
               //Code
            }
        }
        //Logout?
        if(ScreenState==2) {

        }
    }

    private boolean isLoginValid() {
        return true;
    }

}