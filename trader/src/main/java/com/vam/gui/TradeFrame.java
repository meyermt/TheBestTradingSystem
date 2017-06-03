package com.vam.gui;

import com.vam.Main;
import com.vam.client.server.TraderAdminClient;
import com.vam.json.Stock;

import java.awt.*;
import java.awt.event.*;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;

/**
 * Created by ana_b on 5/12/2017.
 */
public class TradeFrame extends JFrame{

    /** The window dimensions for the Frame */
    public static final Dimension FRAME_DIM = new Dimension(800, 600);

    /* The panel for the application **/
    private JPanel tradePanel;

    /* The controller for the trader**/
    private Main mController;

    private List<Stock> stocks = Collections.emptyList();
    private TraderAdminClient adminClient = null;

    //Interação do frame com o controller
    public TradeFrame() {

        //Initialize the frame
        this.setTitle("Global Trader");
        this.setSize(FRAME_DIM);
        this.setFocusable(true);
        this.setResizable(false);
        this.pack();
        this.setVisible(true);
        initTraderPanel();
    }

    /*
        Trader panel that has buttons for options during hand.
    */
    private void initTraderPanel() {
        JPanel traderPanel = new JPanel();
        traderPanel.setLayout(new BoxLayout(traderPanel, BoxLayout.Y_AXIS));
        traderPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initPricePanel();
        initLoginPanel();
    }

    private void initLoginPanel() {
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        loginPanel.setPreferredSize(new Dimension(200, 100));
        setLoginButton(loginPanel);
        this.add(loginPanel, BorderLayout.NORTH);
    }

    private void setLoginButton(JPanel loginPanel) {
        JButton loginButton = new JButton();
        loginButton.setText("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                adminClient = new TraderAdminClient();
                stocks = adminClient.getStocks();
                initComboBoxPanel();
            }
        });
        loginPanel.add(loginButton);
    }

    private void initComboBoxPanel() {
        JPanel comboBoxPanel = new JPanel();
        comboBoxPanel.setLayout(new BoxLayout(comboBoxPanel, BoxLayout.Y_AXIS));
        comboBoxPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        comboBoxPanel.setPreferredSize(new Dimension(200, 100));
        initComboBox(comboBoxPanel);
        this.add(comboBoxPanel, BorderLayout.SOUTH);
    }

    private void initComboBox(JPanel comboBoxPanel) {
        //Create the combo box, select item at index 4.
        //Indices start at 0, so 4 specifies the pig.
        List<String> stockNames = stocks.stream()
                .map(stock -> stock.getStock())
                .collect(Collectors.toList());
        JComboBox stockBox = new JComboBox(stockNames.toArray());
        stockBox.setSelectedIndex(stockNames.size());
        stockBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: Have to update price here. This would be a call to peer
            }
        });
        comboBoxPanel.add(stockBox);
    }

    /*
    betting panel has all money information as well as new deal button.
    */
    private void initPricePanel() {
        JPanel pricePanel = new JPanel();
        pricePanel.setLayout(new BoxLayout(pricePanel, BoxLayout.Y_AXIS));
        pricePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pricePanel.setPreferredSize(new Dimension(200, 100));
        setStockFieldButton(pricePanel);
        this.add(pricePanel, BorderLayout.WEST);
    }

    /*
        allows user to set the amount they would like to bet between rounds.
     */
    private void setStockFieldButton(JPanel pricePanel) {
        JLabel stockLabel = new JLabel();
        stockLabel.setText("Stock to Search");
        pricePanel.add(stockLabel);
        JTextField stockTextField = new JTextField(10);
        stockTextField.setText("");
        stockTextField.setMaximumSize(new Dimension(200, 20));
        pricePanel.add(stockTextField);
        JLabel priceReturnLabel = new JLabel();
        priceReturnLabel.setText("Price: ");
        pricePanel.add(priceReturnLabel);
        JButton betButton = new JButton();
        betButton.setText("Search for Price");
        betButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: should probably add an if-else here that does the client call to the network to see what price is and if stock is not found, JOPtionPane pops up to tell trader
                // TODO: At the very least need to implement getting the price here.
                priceReturnLabel.setText("Price: 4");
            }
        });
        pricePanel.add(betButton);
    }

}
