package com.vam.gui;

import com.vam.Main;
import java.awt.*;
import java.awt.event.*;
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
