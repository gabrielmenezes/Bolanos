package connectfourchampionship;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import org.apache.lucene.util.RamUsageEstimator;
import static org.apache.lucene.util.RamUsageEstimator.humanReadableUnits;

/**
 * Brian Schuette Group Members: Jason Fletchall, Mario Giombi CS540 Section 1 -
 * Jerry Zhu December 4, 2005
 *
 * This class controls the workings of the game and directs the working of the
 * computer players.
 */
public class Connect4Controller extends Thread {

    public static int ROW_SIZE = 6;
    public static int COL_SIZE = 7;

    //game board
    private static int[][] gameBoard = new int[ROW_SIZE][COL_SIZE];

    //gameBoard square values
    private static int PLAYER1_MOVE = 1;  //black player
    private static int PLAYER2_MOVE = -1; //red player
    private static int OPEN_SQUARE = 0;

    //boolean variables to keep track of game state
    public static boolean player1win = false;
    public static boolean player2win = false;
    public static boolean player1move;
    //private String player1Name = "";
    //private String player2Name = "";

    //public variables for next move from computer player(s)
    public int rowNextMove = -1;
    public int colNextMove = -1;

    private IPlayer player1;
    private IPlayer player2;

    public PrintWriter writer;

    /**
     * Constructor. Resets gameBoard, sets the timeoutValue.
     *
     * @param player1starts
     */
    public Connect4Controller(boolean player1starts, IPlayer pl1, IPlayer pl2) {

        player1move = player1starts;
        //initialize the board
        for (int[] gameBoard1 : gameBoard) {
            for (int j = 0; j < gameBoard[0].length; j++) {
                gameBoard1[j] = OPEN_SQUARE;
            }
        }

        //initialize game state
        player1win = false;
        player2win = false;
        //player1move = true;

        //initialized the computer players
        player1 = pl1;
        player2 = pl2;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
        Date date = new Date();
        //System.out.println(dateFormat.format(date)); //2014/08/06 15:59:48
        try {
            writer = new PrintWriter(player1.getTeamName() + "-vs-" + player2.getTeamName() + " - " + dateFormat.format(date) + ".txt", "UTF-8");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "ERROR \nLog file could not be created!", "ERROR", JOptionPane.PLAIN_MESSAGE);
            System.exit(-1);
        }
    }

    /**
     * Reset the game.
     */
    public void ResetGame(boolean flipTurns) {
        for (int i = 0; i < gameBoard.length; i++) {
            for (int j = 0; j < gameBoard[0].length; j++) {
                gameBoard[i][j] = OPEN_SQUARE;
            }
        }
        player1win = false;
        player2win = false;
        //player1move = flipTurns;

    }

    /**
     * Call this function to get and make a computer move. This invokes the
     * player class and gets its move from player.rowNextMove and
     * player.colNextMove. It then makes the move and updates boolean variable
     * player1 to keep track of whose turn it is. Calling method must be
     * checking for the boolean values player1win or player2win.
     */
    public void computer_move() throws Exception {
        int[] cmove = new int[2];
        rowNextMove = -1;
        colNextMove = -1;
        boolean played = false;
        //System.out.println(player1move);
        if (player1move) {

            int column = player1.getNextMove(getBoard());
            long estimatedMemory = RamUsageEstimator.sizeOf(player1);
            if(estimatedMemory>1073741824){
                System.out.println("Player 1 is using " + humanReadableUnits(estimatedMemory) + "of memory and exceeded the limit.\nVictory for player 2");
                JOptionPane.showMessageDialog(Connect4Game.connect4GUI.frame.getContentPane(), "Player 1 is using " + humanReadableUnits(estimatedMemory) + " of memory and exceeded the limit.\nPLAYER 2 WINS!!", "Game Over", JOptionPane.PLAIN_MESSAGE);
                player2win = true;
                throw new Exception("Memory Overload");
            }
            colNextMove = column;

            for (int i = 0; i < ROW_SIZE; i++) {
                if (isLegalMove(i, column)) {
                    rowNextMove = i;
                    Move(rowNextMove, colNextMove, PLAYER1_MOVE);
                    played = true;
                    break;
                }
            }
            if (!played) {
                System.out.println("Impossible play made by player 1 on column " + colNextMove);
                JOptionPane.showMessageDialog(Connect4Game.connect4GUI.frame.getContentPane(), "Invalid move from player 1 on column " + colNextMove + "\nPLAYER 2 WINS!!", "Game Over", JOptionPane.PLAIN_MESSAGE);
                player2win = true;
                throw new Exception("Invalid Move");
            }

        } else {
            int column = player2.getNextMove(getBoard());
            long estimatedMemory = RamUsageEstimator.sizeOf(player2);
            if (estimatedMemory > 1073741824) {
                System.out.println("Player 2 is using " + humanReadableUnits(estimatedMemory) + "of memory and exceeded the limit.\nVictory for player 1");
                JOptionPane.showMessageDialog(Connect4Game.connect4GUI.frame.getContentPane(), "Player 2 is using " + humanReadableUnits(estimatedMemory) + " of memory and exceeded the limit.\nPLAYER 1 WINS!!", "Game Over", JOptionPane.PLAIN_MESSAGE);
                player2win = true;
                throw new Exception("Memory Overload");
            }
            colNextMove = column;

            for (int i = 0; i < ROW_SIZE; i++) {
                if (isLegalMove(i, column)) {
                    rowNextMove = i;
                    Move(rowNextMove, colNextMove, PLAYER2_MOVE);
                    played = true;
                    break;
                }
            }
            if (!played) {
                System.out.println("Impossible play made by player 2 on column " + colNextMove);
                JOptionPane.showMessageDialog(Connect4Game.connect4GUI.frame.getContentPane(), "Invalid move from player 2 on column " + colNextMove + "\n PLAYER 1 WINS!!", "Game Over", JOptionPane.PLAIN_MESSAGE);
                player1win = true;
                throw new Exception("Invalid Move");
            }
        }

        //allow GUI to change this due to timing issues???
        //GUI still expects it to be computer's turn
        player1move = !player1move;

        PrintBoard();
        SaveBoard();
        //System.out.println("Brian " + player1move);
        checkWinner();
        //CompPlayer.recordMove(colNextMove, player2win, player1win);
    }

    /**
     * This function returns whether or not a move is legal.
     *
     * @param row The row of the move to check.
     * @param col The column of the move to check.
     * @return Returns the boolean value indicating whether or not the move is
     * legal.
     */
    public static boolean isLegalMove(int row, int col) {
        //for legal move, must satisfy the following
        if (row < ROW_SIZE && col < COL_SIZE) {
            if (row >= 0 && col >= 0) {
                if (gameBoard[row][col] == OPEN_SQUARE) {
                    //needs to be placed at the bottom
                    if (row == ROW_SIZE - 1) {
                        return true;
                    }
                    //or needs a piece below it
                    if (gameBoard[row + 1][col] != OPEN_SQUARE) {
                        return true;
                    }
                }
            }
        }
        return false;
    }//end isLegalMove

    /**
     * In the case of a win, this method sets one of the boolean variables
     * player1win or player2win to true.
     */
    private void checkWinner() {
        //check for win horizontally
        for (int row = 0; row < ROW_SIZE; row++) {
            for (int col = 0; col < COL_SIZE - 3; col++) { //0 to 3
                if (gameBoard[row][col] == gameBoard[row][col + 1]
                        && gameBoard[row][col] == gameBoard[row][col + 2]
                        && gameBoard[row][col] == gameBoard[row][col + 3]
                        && gameBoard[row][col] != OPEN_SQUARE) {
                    if (gameBoard[row][col] == PLAYER1_MOVE) {
                        player1win = true;
                    } else {
                        player2win = true;
                    }
                }
            }
        }
        //check for win vertically
        for (int row = 0; row < ROW_SIZE - 3; row++) { //0 to 2
            for (int col = 0; col < COL_SIZE; col++) {
                if (gameBoard[row][col] == gameBoard[row + 1][col]
                        && gameBoard[row][col] == gameBoard[row + 2][col]
                        && gameBoard[row][col] == gameBoard[row + 3][col]
                        && gameBoard[row][col] != OPEN_SQUARE) {
                    if (gameBoard[row][col] == PLAYER1_MOVE) {
                        player1win = true;
                    } else {
                        player2win = true;
                    }
                }
            }
        }
        //check for win diagonally (upper left to lower right)
        for (int row = 0; row < ROW_SIZE - 3; row++) { //0 to 2
            for (int col = 0; col < COL_SIZE - 3; col++) { //0 to 3
                if (gameBoard[row][col] == gameBoard[row + 1][col + 1]
                        && gameBoard[row][col] == gameBoard[row + 2][col + 2]
                        && gameBoard[row][col] == gameBoard[row + 3][col + 3]
                        && gameBoard[row][col] != OPEN_SQUARE) {
                    if (gameBoard[row][col] == PLAYER1_MOVE) {
                        player1win = true;
                    } else {
                        player2win = true;
                    }
                }
            }
        }
        //check for win diagonally (lower left to upper right)
        for (int row = 3; row < ROW_SIZE; row++) { //3 to 5
            for (int col = 0; col < COL_SIZE - 3; col++) { //0 to 3
                if (gameBoard[row][col] == gameBoard[row - 1][col + 1]
                        && gameBoard[row][col] == gameBoard[row - 2][col + 2]
                        && gameBoard[row][col] == gameBoard[row - 3][col + 3]
                        && gameBoard[row][col] != OPEN_SQUARE) {
                    if (gameBoard[row][col] == PLAYER1_MOVE) {
                        player1win = true;
                    } else {
                        player2win = true;
                    }
                }
            }
        }

        //check for tie game
        boolean tiegame = true;
        for (int row = 0; row < ROW_SIZE; row++) {
            for (int col = 0; col < COL_SIZE; col++) {
                if (gameBoard[row][col] == 0) {
                    tiegame = false;
                }
            }
        }
        if (tiegame) {
            player1win = true;
            player2win = true;
        }

        //System.out.println("tiegame: " + tiegame);
        //System.out.println("player1win: " + player1win);
        //System.out.println("player2win: " + player2win);
    }//end checkWinner

    /**
     * Call this to print the board to the command window using ASCII characters
     */
    public void PrintBoard() {
        for (int i = 0; i < gameBoard.length; i++) {
            for (int j = 0; j < gameBoard[0].length; j++) {
                System.out.print(gameBoard[i][j] + "\t");
            }
            System.out.println();
        }
        System.out.println("");
    }

    public void SaveBoard() {

        for (int i = 0; i < gameBoard.length; i++) {
            for (int j = 0; j < gameBoard[0].length; j++) {
                writer.print(gameBoard[i][j] + "\t");
            }
            writer.println();
        }
        writer.println("");
    }

    /**
     * This method is used to get the current board state.
     *
     * @return Returns the board in int[][] form.
     */
    public static int[][] getBoard() {
        int[][] newBoard = new int[gameBoard.length][gameBoard[0].length];
        for (int i = 0; i < gameBoard.length; i++) {
            for (int j = 0; j < gameBoard[0].length; j++) {
                newBoard[i][j] = gameBoard[i][j];
            }
        }
        return newBoard;
    }

    /**
     * This function returns the 4 winning spaces.
     *
     * @return Returns the 4 winning moves in int[4][2] form.
     */
    public static int[][] getWinningMoves() {
        //return variable
        int[][] moves = new int[4][2];

        //check for win horizontally
        for (int row = 0; row < ROW_SIZE; row++) {
            for (int col = 0; col < COL_SIZE - 3; col++) { //0 to 3
                if (gameBoard[row][col] == gameBoard[row][col + 1]
                        && gameBoard[row][col] == gameBoard[row][col + 2]
                        && gameBoard[row][col] == gameBoard[row][col + 3]
                        && gameBoard[row][col] != OPEN_SQUARE) {
                    if (gameBoard[row][col] == PLAYER1_MOVE) {
                        player1win = true;
                    } else {
                        player2win = true;
                    }
                    //set winning moves
                    moves[0][0] = row;
                    moves[0][1] = col;
                    moves[1][0] = row;
                    moves[1][1] = col + 1;
                    moves[2][0] = row;
                    moves[2][1] = col + 2;
                    moves[3][0] = row;
                    moves[3][1] = col + 3;
                }
            }
        }
        //check for win vertically
        for (int row = 0; row < ROW_SIZE - 3; row++) { //0 to 2
            for (int col = 0; col < COL_SIZE; col++) {
                if (gameBoard[row][col] == gameBoard[row + 1][col]
                        && gameBoard[row][col] == gameBoard[row + 2][col]
                        && gameBoard[row][col] == gameBoard[row + 3][col]
                        && gameBoard[row][col] != OPEN_SQUARE) {
                    if (gameBoard[row][col] == PLAYER1_MOVE) {
                        player1win = true;
                    } else {
                        player2win = true;
                    }
                    //set winning moves
                    moves[0][0] = row;
                    moves[0][1] = col;
                    moves[1][0] = row + 1;
                    moves[1][1] = col;
                    moves[2][0] = row + 2;
                    moves[2][1] = col;
                    moves[3][0] = row + 3;
                    moves[3][1] = col;
                }
            }
        }
        //check for win diagonally (upper left to lower right)
        for (int row = 0; row < ROW_SIZE - 3; row++) { //0 to 2
            for (int col = 0; col < COL_SIZE - 3; col++) { //0 to 3
                if (gameBoard[row][col] == gameBoard[row + 1][col + 1]
                        && gameBoard[row][col] == gameBoard[row + 2][col + 2]
                        && gameBoard[row][col] == gameBoard[row + 3][col + 3]
                        && gameBoard[row][col] != OPEN_SQUARE) {
                    if (gameBoard[row][col] == PLAYER1_MOVE) {
                        player1win = true;
                    } else {
                        player2win = true;
                    }
                    //set winning moves
                    moves[0][0] = row;
                    moves[0][1] = col;
                    moves[1][0] = row + 1;
                    moves[1][1] = col + 1;
                    moves[2][0] = row + 2;
                    moves[2][1] = col + 2;
                    moves[3][0] = row + 3;
                    moves[3][1] = col + 3;
                }
            }
        }
        //check for win diagonally (lower left to upper right)
        for (int row = 3; row < ROW_SIZE; row++) { //3 to 5
            for (int col = 0; col < COL_SIZE - 3; col++) { //0 to 3
                if (gameBoard[row][col] == gameBoard[row - 1][col + 1]
                        && gameBoard[row][col] == gameBoard[row - 2][col + 2]
                        && gameBoard[row][col] == gameBoard[row - 3][col + 3]
                        && gameBoard[row][col] != OPEN_SQUARE) {
                    if (gameBoard[row][col] == PLAYER1_MOVE) {
                        player1win = true;
                    } else {
                        player2win = true;
                    }
                    //set winning moves
                    moves[0][0] = row;
                    moves[0][1] = col;
                    moves[1][0] = row - 1;
                    moves[1][1] = col + 1;
                    moves[2][0] = row - 2;
                    moves[2][1] = col + 2;
                    moves[3][0] = row - 3;
                    moves[3][1] = col + 3;
                }
            }
        }
        //System.out.println(moves[0][0] + " " + moves[0][1]);
        //System.out.println(moves[1][0] + " " + moves[1][1]);
        //System.out.println(moves[2][0] + " " + moves[2][1]);
        //System.out.println(moves[3][0] + " " + moves[3][1]);
        return moves;
    }

    /**
     * Tells the computer player to save the information it has gathered
     */
    /*public static void savePlayer() {
     CompPlayer.saveTree();
     }*/
    private void Move(int row, int col, int player) {
        //System.out.println("move call");
        if (isLegalMove(row, col)) {
            //if player == 1 (the black player/the first player)
            if (player == 1) {
                gameBoard[row][col] = PLAYER1_MOVE;
            } else {
                gameBoard[row][col] = PLAYER2_MOVE;
            }
        } else {
            //PrintBoard();
            if (player == 1) {
                System.out.println("Player 1 made an invalid move." + row + " " + col);
                player2win = true;
            } else {
                System.out.println("Player 2 made an invalid move." + row + " " + col);
                player1win = true;
            }
        }
    }

}//end Connect4Controller class
