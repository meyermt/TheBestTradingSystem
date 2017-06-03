package com.vam.gui;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Created by ana_b on 5/12/2017.
 */
public class TradePanel extends JPanel{

    /**
     * The current state of the game (Main Menu, Game, High Scores, Game Over
     */
    private int ScreenState;


    public TradePanel() {

        this.setPreferredSize(TradeFrame.FRAME_DIM);
        ScreenState=0;
    }

    public void setState(int tradeState) {
        ScreenState = tradeState;
    }

    @Override
    public void paintComponent(Graphics g) {
        // Call the super paintComponent of the panel
        super.paintComponent(g);
        g.setColor(Color.black);
        g.fillRect(0, 0, TradeFrame.FRAME_DIM.width, TradeFrame.FRAME_DIM.height);
        g.setColor(Color.WHITE);
        g.setFont(new Font("ARIAL", Font.BOLD, 20));
        //Main Menu
        if(ScreenState==0) {
            g.drawString("1) PLAY GAME",400,400);
            g.drawString("2) HIGH SCORES",400,500);
        }
        //Game
        if (ScreenState == 1) {
            //Draw the scoreboard

            g.drawString("Points:" + Game.getmPoints() + " Level:" + Game.getLevel(), 10, 50);
            //Start redrawing all the objects of the game.
            ArrayList<Sprite> sprites = Game.getDrawableSprites();


            for (Sprite sprite : sprites) {
                sprite.draw(g);
            }
        }
        //High Scores
        if(ScreenState==2) {
            g.drawString("High Scores", 400, 100);
            try {
                ArrayList<Integer> list = HighScorePlace.getTopTenScore();
                for (int i = 0; i < list.size(); i++) {
                    g.drawString(i+1 + ")" + list.get(i), 400, 200 + i * 50);
                }
            } catch (FileNotFoundException e) {
                g.drawString("No scores have been added or problem with the file. Please reload Galaga.",200,200);
            }
        }
        //Game Over
        if(ScreenState==3){
            g.drawString("GAME OVER",250,200);
            g.drawString("Points: " + Game.getmPoints() + " Level: " + Game.getLevel(),200,300);
        }
    }
}
