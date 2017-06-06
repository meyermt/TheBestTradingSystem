package com.vam;

import com.vam.json.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private boolean isLoginValid;
    private JComboBox<String> stockValue;
    private JTextField priceValue;
    private JTextField quantityValue;
    private AdminTraderResponse mLoginResult;
    private TraderPeerResponse mCurrentConsResult;
    private TraderPeerResponse mLastSaleResult;
    private Logger logger = LoggerFactory.getLogger(TradePanel.class);
    private static final String IP = "127.0.0.1";
    private JLabel resultAlert;

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
            int rows = mLoginResult.getStocks().size();

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
            stockValue = new JComboBox<String>();


            JLabel price = new JLabel("Price($):");
            priceValue = new JTextField(15);
            priceValue.setEditable(false);
            JLabel quantity = new JLabel("Quantity");
            quantityValue = new JTextField(15);
            priceValue.setEditable(false);
            JTextArea availableStocks = new JTextArea(rows, 1);
            String concatStock = "";
            for (Stock c : mLoginResult.getStocks()) {
                if (!c.isFund()) {
                    concatStock += c.getStock() + "\n";
                    stockValue.addItem(c.getStock());
                }
            }
            concatStock += "Mutal_Fund_Banking_1\n";
            concatStock += "Mutal_Fund_Energy_1\n";
            concatStock += "Mutal_Fund_Diversified_1\n";
            stockValue.addItem("Mutal_Fund_Banking_1");
            stockValue.addItem("Mutal_Fund_Energy_1");
            stockValue.addItem("Mutal_Fund_Diversified_1");
            resultAlert= new JLabel("Result:");

            JScrollPane scroll = new JScrollPane(availableStocks);
            scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            add(usernameLabel, BorderLayout.NORTH);
//            add(scroll,BorderLayout.WEST);
            add(stock, BorderLayout.CENTER);
            add(stockValue, BorderLayout.CENTER);
            add(price, BorderLayout.CENTER);
            add(priceValue, BorderLayout.CENTER);
            add(quantity, BorderLayout.CENTER);
            add(quantityValue, BorderLayout.CENTER);
            add(consPrice, BorderLayout.CENTER);
            add(sell, BorderLayout.CENTER);
            add(buy, BorderLayout.CENTER);
            add(resultAlert,BorderLayout.CENTER);
            //   add(scroll,BorderLayout.SOUTH);
        }
        //Logout?
        if (ScreenState == 2) {

        }
    }

    private void changeState() {
        removeAll();
        panel();
        updateUI();
        repaint();
    }

    private class LoginListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            String country = (String) mCountry.getSelectedItem();
            logger.info("country i got was {}", country);
            TraderAdminRequest request = new TraderAdminRequest(IP, 1346, TraderAdminAction.LOGIN, country, "");
            TraderClient client = new TraderClient(IP, 1347, request, IP, 1346);
            client.sendLoginRequest(request);
        }
    }

    public void processLogin(AdminTraderResponse adminTraderResponse) {
        mLoginResult = adminTraderResponse;
        if (adminTraderResponse.getCode().equals(AdminTraderResponseCode.OK)) {
            isLoginValid = true;
        } else if (adminTraderResponse.getCode().equals(AdminTraderResponseCode.NO_AVAILABLE_PEER)) {
            String sadMessage = JOptionPane.showInputDialog("No avaliable peers");
        } else {
            System.out.println("Invalid action");
        }
        if (isLoginValid) {
            mUsername = mUsernameText.getText();
            mPassword = mPasswordText.getText();
            ScreenState = 1;
            changeState();
        } else {
            JLabel problem = new JLabel("There's a problem with your login");
        }
    }

    private class ConsultListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            // String selectedStock = (String) stockValue.getSelectedItem();
            String selectedStock = "Royal Bank of Canada";
            logger.info("selected stock is " + selectedStock);
            double price = 0;
            int quantity = 0;
            Stock stockItem = mLoginResult.getStocks().stream()
                    .filter(stock -> stock.getStock().equals(selectedStock))
                    .findFirst().orElseThrow(() -> new RuntimeException("could not find stock " + selectedStock + " in list"));
            logger.info(stockItem.toString());
            TraderPeerRequest request = new TraderPeerRequest(IP, 12345, TraderAction.CONSULT, stockItem, stockItem.getContinent(),
                    stockItem.getMarket(), quantity, price);
            logger.info("about to request consulting from {}", mLoginResult.getPeerPort());
            logger.info("sending {}", request.toString());
            TraderClient client = new TraderClient(IP, mLoginResult.getPeerPort(), request, IP, 12345);
            client.sendPeerRequest(request);
        }
    }

    private class SellListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            String selectedStock = (String) stockValue.getSelectedItem();
            double price = 0;
            int quantity = 0;
            try {
                price = Double.parseDouble(priceValue.getText());
                quantity = Integer.parseInt(quantityValue.getText());
            } catch (NumberFormatException e) {
                String sadMessage = JOptionPane.showInputDialog("Price and quantity need to be numerical");
            }
            Stock stockItem = mLoginResult.getStocks().stream()
                    .filter(stock -> stock.getStock().equals(selectedStock))
                    .findFirst().orElseThrow(() -> new RuntimeException("could not find stock " + selectedStock + " in list"));
            TraderPeerRequest request = new TraderPeerRequest(IP, 12345, TraderAction.SELL, stockItem, stockItem.getContinent(),
                    stockItem.getMarket(), quantity, price);
            TraderClient client = new TraderClient(IP, mLoginResult.getPeerPort(), request, IP, 12345);
            client.sendPeerRequest(request);
        }
    }

    private class BuyListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            //String selectedStock = (String) stockValue.getSelectedItem();
            String selectedStock = "Royal Bank of Canada";
            double price = mCurrentConsResult.getPrice();
            int quantity = 0;
            logger.info("price before is {} and quantity is {}", priceValue.getText(), quantityValue.getText());
            try {
                //price = Double.parseDouble(priceValue.getText());
                quantity = Integer.parseInt(quantityValue.getText());
            } catch (NumberFormatException e) {
                String sadMessage = JOptionPane.showInputDialog("Price and quantity need to be numerical");
            }
            logger.info("price is {} and quantity is {}", price, quantity);
            Stock stockItem = mLoginResult.getStocks().stream()
                    .filter(stock -> stock.getStock().equals(selectedStock))
                    .findFirst().orElseThrow(() -> new RuntimeException("could not find stock " + selectedStock + " in list"));
            TraderPeerRequest request = new TraderPeerRequest(IP, 12345, TraderAction.BUY, stockItem, stockItem.getContinent(),
                    stockItem.getMarket(), quantity, price);
            TraderClient client = new TraderClient(IP, mLoginResult.getPeerPort(), request, IP, 12345);
            client.sendPeerRequest(request);
        }
    }

    public void processConsultResponse(TraderPeerResponse response) {
        mCurrentConsResult = response;
        System.out.println("I'm processing the response for :"+response);
        logger.info("processing a consult request from my peer");
        resultAlert.setText(mCurrentConsResult.getSucceedMessage());
        //??
        stockValue.setSelectedItem(mCurrentConsResult.getStock());
        priceValue.setText("" + mCurrentConsResult.getPrice());
        priceValue.setEditable(false);
        quantityValue.setEditable(true);
        repaint();
    }

    public void processSellResponse(TraderPeerResponse response) {
        logger.info("processing a sell request from my peer");
        System.out.println("I'm processing the response for :"+response);
        logger.info("processing a consult request from my peer");
        resultAlert.setText(response.getSucceedMessage());
        priceValue.setText("");
        quantityValue.setText("");
        priceValue.setEditable(false);
        quantityValue.setEditable(true);
        repaint();
    }

    public void processBuyResponse(TraderPeerResponse response) {
        logger.info("processing a buy request from my peer");
        System.out.println("I'm processing the response for :"+response);
        logger.info("processing a consult request from my peer");
        resultAlert.setText(response.getSucceedMessage());
        priceValue.setText("");
        quantityValue.setText("");
        priceValue.setEditable(false);
        quantityValue.setEditable(true);
        repaint();
    }

//    private void processResult(String process) {
//        if (process.equals("consult")) {
//            JLabel resultAlert =new JLabel( mCurrentConsResult.getSucceedMessage());
//            add(resultAlert);
//            repaint();
//            if(mCurrentConsResult.succeed()){
//                stockValue.setSelectedItem(mCurrentConsResult.getStockName());
//                priceValue.setText("" + mCurrentConsResult.getPrice());
//                priceValue.setEditable(false);
//                refreshFields();
//            }
//        }
//        else if (process.equals("sell")){
//            if(mLastSaleResult.succeed()){
//                refreshFields();
//            }
//        }
//        else if (process.equals("buy")){
//            if(mLastSaleResult.succeed()){
//                refreshFields();
//            }
//        }
//        else{
//            throw new IllegalStateException("Process must be consult, sell or buy");
//        }
//    }
//}

//        private void processMutualFund(String fundName, int quantity, double totalPrice) {
//            MutualFund fund = new MutualFund(fundName);
//            HashMap<String, Stock> listOfStocks = fund.getComposition();
//            ArrayList<Boolean> confirmation = new ArrayList<>();
//            for (Stock c : listOfStocks.values()) {
//                int individualQuantity = (quantity * (c.getPercentage() / 100));
//                double indivualPrice = (totalPrice * (c.getPercentage() / 100));
//                TraderPeerRequest request = new TraderPeerRequest("localhost", 1346, TraderAction.RESERVE, c, c.getContinent(), c.getMarket(), individualQuantity, indivualPrice);
//                TraderClient client = new TraderClient("localhost", mLoginResult.getPeerPort(), request, "localhost", 1346);
//                if (client.getmResponse() instanceof TraderPeerResponse) {
//                    if (!((TraderPeerResponse) client.getmResponse()).isSucceed()) {
//                        //TODO this action
//                        processResult("failed");
//                    }
//                    //TODO complete results
//                    //mLastSaleResult=
//                }
//                //TODO this action
//                processResult("buyMF");
//            }

//        }

//        private void processMutualFundConsult(String fundName, int quantity, double totalPrice) {
//            MutualFund fund = new MutualFund(fundName);
//            HashMap<String, Stock> listOfStocks = fund.getComposition();
//            ArrayList<Boolean> confirmation = new ArrayList<>();
//            for (Stock c : listOfStocks.values()) {
//                int individualQuantity = (quantity * (c.getPercentage() / 100));
//                double indivualPrice = (totalPrice * (c.getPercentage() / 100));
//                TraderPeerRequest request = new TraderPeerRequest("localhost", 1346, TraderAction.CONSULT, c, c.getContinent(), c.getMarket(), individualQuantity, indivualPrice);
//                TraderClient client = new TraderClient("localhost", mLoginResult.getPeerPort(), request, "localhost", 1346);
//                if (client.getmResponse() instanceof TraderPeerResponse) {
//                    if (!((TraderPeerResponse) client.getmResponse()).isSucceed()) {
//                        //TODO this action
//                        processResult("failed");
//                    }
//                    //TODO complete results
//                    //mLastSaleResult=
//                }
//                //TODO this action
//                processResult("buyMF");
//            }

        //       }


//    private void processMutualFundConfirm(String fundName, int quantity, double totalPrice) {
//        MutualFund fund = new MutualFund(fundName);
//        HashMap<String, Stock> listOfStocks = fund.getComposition();
//        ArrayList<Boolean> confirmation = new ArrayList<>();
//        for (Stock c : listOfStocks.values()) {
//            int individualQuantity = (quantity * (c.getPercentage() / 100));
//            double indivualPrice = (totalPrice * (c.getPercentage() / 100));
//            TraderPeerRequest request = new TraderPeerRequest("localhost", 1346, TraderAction.BUY, c, c.getContinent(), c.getMarket(), individualQuantity, indivualPrice);
//            TraderClient client = new TraderClient("localhost", mLoginResult.getPeerPort(), request, "localhost", 1346);
//            if (client.getmResponse() instanceof TraderPeerResponse) {
//                if (!((TraderPeerResponse) client.getmResponse()).isSucceed()) {
//                    //TODO this action
//                    processResult("failed");
//                }
//                //TODO complete results
//                //mLastSaleResult=
//            }
//            //TODO this action
//            processResult("buyMF");
//        }

//    }
}
