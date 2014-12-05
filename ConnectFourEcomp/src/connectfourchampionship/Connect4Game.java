/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connectfourchampionship;

import javax.swing.JOptionPane;

/**
 *
 * @author MarcioNote
 */
public class Connect4Game {

    public static GUI connect4GUI;

    public static void newGame(IPlayer player1, IPlayer player2) {
        do {
            Object[] options = {"Player 2", "Player 1"};
            int player = JOptionPane.showOptionDialog(connect4GUI.frame,
                    "Choose player to go first",
                    "New Game",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null, //do not use a custom Icon
                    options, //the titles of buttons
                    options[1]);
            boolean player1starts;
            if (Math.abs(player) == 1) {
                //player 1 starts
                player1starts = true;
            } else {
                //player 2 starts
                player1starts = false;
            }
            //System.out.println(player);
            connect4GUI = new GUI(player1starts, player1, player2);
            connect4GUI.startGame();
        } while (JOptionPane.showConfirmDialog(connect4GUI.frame, "Would you like to play again?", "New Game", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.YES_OPTION);

    }
}
