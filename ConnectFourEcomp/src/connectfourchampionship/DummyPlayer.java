/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connectfourchampionship;

import java.util.Random;

/**
 *
 * @author MarcioNote
 */
public class DummyPlayer implements IPlayer {

    @Override
    public int getNextMove(int[][] gameBoard) {
        //if(true)return 0;
        while (true) {
            int possiblePlay = new Random().nextInt(gameBoard[0].length);
            for (int i = 0; i < gameBoard.length; i++) {
                if(isLegalMove(gameBoard, i, possiblePlay)){
                    return possiblePlay;
                }
            }
        }
    }

    public boolean isLegalMove(int[][] gB, int row, int col) {
        //for legal move, must satisfy the following
        int ROW_SIZE = gB.length;
        int COL_SIZE = gB[0].length;
        if (row < ROW_SIZE && col < COL_SIZE) {
            if (row >= 0 && col >= 0) {
                if (gB[row][col] == 0) {
                    //needs to be placed at the bottom
                    if (row == ROW_SIZE - 1) {
                        return true;
                    }
                    //or needs a piece below it
                    if (gB[row + 1][col] != 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public String getTeamName() {
        return "Dummy";
    }

}
