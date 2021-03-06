package com.vam;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Created by ana_b on 5/12/2017.
 */
public class TradeFrame extends JFrame{

    /** The window dimensions for the Frame */
    public static final Dimension FRAME_DIM = new Dimension(1000, 400);

    /* The panel for the application **/
    private TradePanel mPanel;

    /* The controller for the trader**/
    private Main mController;

    //Interação do frame com o controller
    public TradeFrame(Main controller) {

        enableEvents(AWTEvent.WINDOW_EVENT_MASK);

        //Set the trader controller for the Frame
        this.mController = controller;

        try {
            // Try to initialize the panel
            initPanel();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Initialize the frame
        this.setTitle("Global Trader");
        this.setSize(FRAME_DIM);
        this.setFocusable(true);
        this.setResizable(false);
        this.pack();
        this.setVisible(true);
    }


    /**
     * Draws the current information to the window
     */
    public void draw() {
        this.mPanel.repaint();
    }
    private void initPanel() throws Exception {

        JPanel contentPane = (JPanel) this.getContentPane();
        contentPane.setLayout(new BorderLayout());
        //Create a new Panel for the controller
        this.mPanel = new TradePanel();

        //Let the controller be the listener for the all actions that happen on the panel
        //Add the panel to the window's content panel.
        contentPane.add(mPanel);
    }

    public void setmPanel(int tradeState) {
        mPanel.setState(tradeState);
    }

    @Override
    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        //Exit the game if the window closed button is closed.
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            System.exit(0);
        }
    }

}