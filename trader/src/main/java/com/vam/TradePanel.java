package com.vam;

import com.vam.json.Country;
import com.vam.json.Stock;
import com.vam.json.TraderAdminAction;
import com.vam.json.TraderAdminRequest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ana_b on 5/12/2017.
 */
public class TradePanel extends JPanel {

    /**
     * The current state of the Trader (Login, Main Menu, Logout)
     */
    private int ScreenState = 0;
    private String mUsername;
    private String mPassword;
    private Map<String, Stock> mStock;
    private int width = TradeFrame.FRAME_DIM.width;
    private int height = TradeFrame.FRAME_DIM.height;
    private JTextField mUsernameText;
    private JTextField mPasswordText;
    private JComboBox<String> mCountry;

    public TradePanel() {

        this.setPreferredSize(TradeFrame.FRAME_DIM);
        //this.setLayout(new GridLayout());
        this.mStock = new HashMap<String, Stock>();
        ScreenState = 0;
        panel();
    }

    public void setState(int tradeState) {
        ScreenState = tradeState;
    }

    public void panel() {

        /*g.setColor(Color.black);
        g.fillRect(0, 0, TradeFrame.FRAME_DIM.width, TradeFrame.FRAME_DIM.height);
        g.setColor(Color.WHITE);
        g.setFont(new Font("ARIAL", Font.BOLD, 16));
*/
        //Login
        if (ScreenState == 0) {
            JLabel usernameLabel = new JLabel("Username:*");
            JLabel passwordLabel = new JLabel("Password:*");
            mUsernameText = new JTextField(15);
            mPasswordText = new JTextField(15);
            JButton log = new JButton("Login");
            JLabel countryLabel = new JLabel("Country:*");
            mCountry = new JComboBox<String>();

            for (Country c : Country.values()) {
                mCountry.addItem(c.getName());
            }
            ActionListener dealListener = new TradePanel.LoginListener();
            log.addActionListener(dealListener);


            add(usernameLabel, BorderLayout.CENTER);
            add(mUsernameText, BorderLayout.CENTER);
            add(passwordLabel, BorderLayout.CENTER);
            add(mPasswordText, BorderLayout.CENTER);
            add(countryLabel, BorderLayout.CENTER);
            add(mCountry, BorderLayout.CENTER);
            add(log, BorderLayout.CENTER);

        }

        if (ScreenState == 1) {

            //Draw the main menu
            JLabel usernameLabel = new JLabel("Hello " + mUsername);
            JButton consPrice = new JButton("Estimate");
            ActionListener consListener = new TradePanel.ConsultListener();
            consPrice.addActionListener(consListener);

            JButton sell = new JButton("Sell");
            ActionListener sellListener = new TradePanel.SellListener();
            sell.addActionListener(sellListener);

            JButton buy = new JButton("Buy");
            ActionListener buyListener = new TradePanel.BuyListener();
            buy.addActionListener(buyListener);

            JLabel stock = new JLabel("Stock:");
            JTextField stock_value = new JTextField(15);
            JLabel price = new JLabel("Price:");
            JTextField price_value = new JTextField(15);
            JLabel quantity = new JLabel("Quantity");
            JTextField quantity_value = new JTextField(15);
            int rows = mStock.size();
            JTextArea availableStocks = new JTextArea(rows, 1);
            for (Stock stockName : mStock.values()) {
                //Code
            }

            add(usernameLabel, BorderLayout.NORTH);
            add(availableStocks,BorderLayout.WEST);
            add(stock, BorderLayout.CENTER);
            add(stock_value, BorderLayout.CENTER);
            add(price, BorderLayout.CENTER);
            add(price_value, BorderLayout.CENTER);
            add(quantity, BorderLayout.CENTER);
            add(quantity_value, BorderLayout.CENTER);
            add(consPrice, BorderLayout.CENTER);
            add(sell, BorderLayout.CENTER);
            add(buy, BorderLayout.CENTER);

        }
        //Logout?
        if (ScreenState == 2) {

        }
    }

    private boolean isLoginValid() {
        return true;
    }

    private void changeState() {
        removeAll();
        panel();
        updateUI();
        repaint();
    }


    private class LoginListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            String country= (String) mCountry.getSelectedItem();
            TraderAdminRequest request= new TraderAdminRequest("localhost",1346, TraderAdminAction.LOGIN,country,"");
            TraderAdminClient client = new TraderAdminClient("localhost",1347,request,"localhost",1346);

            if (isLoginValid()) {
                mUsername = mUsernameText.getText();
                mPassword = mPasswordText.getText();

                ScreenState = 1;
                changeState();
            } else {
                JLabel problem = new JLabel("There's a problem with your login");
            }
        }
    }

    private class ConsultListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {

            if (isLoginValid()) {
                ScreenState = 1;
                changeState();
            } else {
                JLabel problem = new JLabel("There's a problem with your login");
            }
        }
    }

    private class SellListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {



            if (isLoginValid()) {

                ScreenState = 1;
                changeState();
            } else {
                JLabel problem = new JLabel("There's a problem with your login");
            }
        }
    }

    private class BuyListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {

            if (isLoginValid()) {
                ScreenState = 1;
                changeState();
            } else {
                JLabel problem = new JLabel("There's a problem with your login");
            }
        }
    }
}