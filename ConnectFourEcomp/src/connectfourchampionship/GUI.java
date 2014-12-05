package connectfourchampionship;

/**
 * Jason Fletchall Group Members: Mario Giombi, Brian Schuette CS540 Section 1 -
 * Jerry Zhu December 4, 2005
 *
 * An object of this class is a GUI for the Connect 4 Game. It instantiates a
 * Connect4Controller that handles both the rules of the game (e.g. game
 * winners) and computer players. The GUI itself consists of the game board,
 * with each slot represented by a JButton, plus a menu bar allowing the user to
 * quit or start a new game. The GUI prevents human players from making illegal
 * moves by enabling only the JButtons that represent slots where checkers could
 * possibly be placed, according to the rules and physics of the game. In
 * effect, Only the lowest unfilled slots have their JButtons enabled.
 */
import static connectfourchampionship.Connect4Controller.player1win;
import static connectfourchampionship.Connect4Controller.player2win;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GUI {

    private static final int ROW_SIZE = Connect4Controller.ROW_SIZE;  //number of rows
    private static final int COL_SIZE = Connect4Controller.COL_SIZE;  //number of columns
    private String gameType;                //hh for human-human, hc for human-computer, cc for computer-computer
    //private boolean humanMove;              //is it a human's turn?
    private boolean player1move;            //is it player 1's turn?

    private Icon openIcon = new ImageIcon(getClass().getResource("images/Open.GIF"));             //open slot
    private Icon redIcon = new ImageIcon(getClass().getResource("images/Red.GIF"));               //slot with red checker
    private Icon blackIcon = new ImageIcon(getClass().getResource("images/Black.GIF"));           //slot with black checker
    private Icon openUnavailableIcon = new ImageIcon(getClass().getResource("images/Open.GIF"));     //open unavailable slot
    private Icon redRollIcon = new ImageIcon(getClass().getResource("images/RedRoll.GIF"));       //red slot with mouseover
    private Icon blackRollIcon = new ImageIcon(getClass().getResource("images/BlackRoll.GIF"));   //black slot with mouseover
    private Icon redWinIcon = new ImageIcon(getClass().getResource("images/RedWin.GIF"));         //red winning slot
    private Icon blackWinIcon = new ImageIcon(getClass().getResource("images/BlackWin.GIF"));     //black winning slot

    public static JFrame frame;                   //GUI Window
    private JMenuBar menuBar;               //the menu bar (with file menu)
    private JMenu fileMenu;                 //file menu in the flesh

    private JButton[][] boardButtons;       //the slots in the game
    private ButtonListener[][] buttonListener;    //slot button listeners

    private JPanel panel;                   //panel for slot buttons

    private Connect4Controller controller;        //the connect 4 game controller
    
//-----------------------------------------------------------------------
    /**
     * Set up the GUI components, controller
     *
     * @param p1starts
     */
    public GUI(boolean p1starts, IPlayer player1, IPlayer player2) {
        player1move = p1starts;
        controller = new Connect4Controller(p1starts, player1, player2);

        /*Set up basic frame*/
        JFrame.setDefaultLookAndFeelDecorated(true);
        frame = new JFrame("Connect 4");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    //have the program handle close window

        /*Add menu bar*/
        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        JMenuItem newGameItem = new JMenuItem("New Game");             //new game menu item
        JMenuItem quitGameItem = new JMenuItem("Quit");                //quit game menu item
        MenuItemListener newGameListener = new MenuItemListener();     //listens for new game
        MenuItemListener quitGameListener = new MenuItemListener();    //listens for quit game
        newGameItem.addActionListener(newGameListener);
        quitGameItem.addActionListener(quitGameListener);
        //fileMenu.add(newGameItem);
        fileMenu.add(quitGameItem);
        menuBar.add(fileMenu);
        frame.setJMenuBar(menuBar);

        MenuListener menuListener = new MenuListener();              //listens for outer window options
        frame.addWindowListener(menuListener);

        buttonListener = new ButtonListener[ROW_SIZE][COL_SIZE];     //button-press listeners
        boardButtons = new JButton[ROW_SIZE][COL_SIZE];              //all buttons
        panel = new JPanel(new GridLayout(ROW_SIZE, COL_SIZE));       //7 x 7 grid
        
        newGameBoard();           //set up fresh game board 

    }  //constructor

//-----------------------------------------------------------------------
    /**
     * Sets up new game board
     */
    public void newGameBoard() {
        panel = new JPanel(new GridLayout(ROW_SIZE, COL_SIZE));       //7 x 7 grid

        int i, j;
        for (i = 0; i < ROW_SIZE; i++) {
            for (j = 0; j < COL_SIZE; j++) {
                boardButtons[i][j] = new JButton(openIcon);                  //normal icon = clear
                boardButtons[i][j].setRolloverIcon(blackRollIcon);           //rollover icon = black rollover
                buttonListener[i][j] = new ButtonListener(i, j);              //initialize this button's listener
                boardButtons[i][j].addActionListener(buttonListener[i][j]);  //assign above listener to this button
                boardButtons[i][j].setBackground(Color.YELLOW);              //background color = yellow
                panel.add(boardButtons[i][j]);
                if (i < ROW_SIZE - 1) {
                    boardButtons[i][j].setDisabledIcon(openUnavailableIcon);    //non-bottom disabled icon = unavailable  
                    boardButtons[i][j].setEnabled(false);                       //disable non-bottom buttons 
                } //end if
                else {
                    boardButtons[i][j].setDisabledIcon(blackIcon);      //disabled icon = black checker
                }  //end else
            }
        }

        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));      //make a small border around game

        frame.getContentPane().add(panel);
        frame.pack();
        frame.setVisible(true);
        frame.setEnabled(true);

        //player1move = true;
    }  //newGame

    /**
     * Displays the window on the screen
     */
    public void displayGUI() {

        frame.setEnabled(true);

        /*Let controller handle a computer move*/
        //frame.setEnabled(false);
        try {
            controller.computer_move();       //get computer's move from controller 
            boardButtons[controller.rowNextMove][controller.colNextMove].doClick();      //computer "clicks" button at specified location
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
//	    displayGUI();              //display updated GUI
        //end if
    }  //displayGUI

    public void startGame() {
        while (player1win == false && player2win == false) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Connect4Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
            displayGUI();
        }
        controller.writer.close();
    }

//---------------------------------------------------------------------------------------------------
//---------------------------------------Supporting Classes------------------------------------------
//---------------------------------------------------------------------------------------------------

    /*Listens to slot buttons*/
    class ButtonListener implements ActionListener {

        private int myRow;         //row of button that this is listening to
        private int myCol;         //col             "

        public ButtonListener() {
            myRow = 0;
            myCol = 0;
        }  //default constructor

        public ButtonListener(int inRow, int inCol) {
            myRow = inRow;
            myCol = inCol;
        }  //constructor

        /*invoked when an active slot button has been clicked*/
        public void actionPerformed(ActionEvent actionEvent) {
            if (player1move) {
                boardButtons[myRow][myCol].setDisabledIcon(redIcon);
                boardButtons[myRow][myCol].setRolloverIcon(redRollIcon);
            } //end if
            else {
                boardButtons[myRow][myCol].setDisabledIcon(blackIcon);
                boardButtons[myRow][myCol].setRolloverIcon(blackRollIcon);
            }
            boardButtons[myRow][myCol].setEnabled(false);  //disable this listener's button
            if (myRow > 0) {
                boardButtons[myRow - 1][myCol].setEnabled(true);  //enable button above this listener's button
            }  //end if

            /*switch button icons to other player's*/
            int i, j;
            for (i = 0; i < ROW_SIZE; i++) {
                for (j = 0; j < COL_SIZE; j++) {
                    if (boardButtons[i][j].isEnabled()) {
                        if (player1move) {
                            boardButtons[i][j].setDisabledIcon(redIcon);
                            boardButtons[i][j].setRolloverIcon(redRollIcon);
                        } //end if
                        else {
                            boardButtons[i][j].setDisabledIcon(blackIcon);
                            boardButtons[i][j].setRolloverIcon(blackRollIcon);
                        }  //end else
                    }  //end if
                }  //end for cols
            }  //end for rows

            if (controller.player1win || controller.player2win) {
                /*Disable all open slots*/
                for (i = 0; i < ROW_SIZE; i++) {
                    for (j = 0; j < COL_SIZE; j++) {
                        if (boardButtons[i][j].isEnabled()) {
                            boardButtons[i][j].setDisabledIcon(openIcon);
                            boardButtons[i][j].setEnabled(false);
                        }  //end if
                    }  //end for cols
                }  //end for rows

                /*Display graphic on winning checkers, text box*/
                int[][] winningSlots = controller.getWinningMoves();
                if (controller.player1win) {
                    if (controller.player2win) {
                        frame.setEnabled(true);
                        JOptionPane.showMessageDialog(frame.getContentPane(), "It's a tie!  Everybody's a winner!", "Game Over", JOptionPane.PLAIN_MESSAGE);
                    } //end if
                    else {
                        for (i = 0; i < winningSlots.length; i++) {
                            boardButtons[winningSlots[i][0]][winningSlots[i][1]].setDisabledIcon(redWinIcon);
                        }
                        JOptionPane.showMessageDialog(frame.getContentPane(), "Player 1 Connects 4!", "Game Over", JOptionPane.PLAIN_MESSAGE);
                    }  //end else
                } //end if
                else if (controller.player2win) {
                    for (i = 0; i < winningSlots.length; i++) {
                        boardButtons[winningSlots[i][0]][winningSlots[i][1]].setDisabledIcon(blackWinIcon);
                    }
                    frame.setEnabled(true);
                    JOptionPane.showMessageDialog(frame.getContentPane(), "Player 2 Connects 4!", "Game Over", JOptionPane.PLAIN_MESSAGE);
                }  //end else if
            }  //end if

            player1move = !player1move;

//            if (humanMove) {
//                if (gameType.equals("hc") || gameType.equals("hm")) {
//                    humanMove = false;
//                }
//                displayGUI();
//            }
        }  //actionPerformed
    }  //class ButtonListener


    /*Listen to window buttons*/
    class MenuListener implements WindowListener {

        public void windowClosed(WindowEvent we) {
            //controller.savePlayer();       //save game for player
            System.exit(0);
        }

        public void windowClosing(WindowEvent we) {
            windowClosed(we);
        }

        public void windowActivated(WindowEvent we) {
        }

        public void windowDeactivated(WindowEvent we) {
        }

        public void windowDeiconified(WindowEvent we) {
        }

        public void windowIconified(WindowEvent we) {
        }

        public void windowOpened(WindowEvent we) {
        }

    }


    /*Listens to a menu item*/
    class MenuItemListener implements ActionListener {

        public void actionPerformed(ActionEvent ae) {
            String selection = ae.getActionCommand();
            if (selection.equals("New Game")) {
                controller.ResetGame(true);              //tell controller it's a new game
                //frame.getContentPane().remove(panel);    //clear GUI panel
                newGameBoard();
                //displayGUI();
                startGame();
            } //end if
            else if (selection.equals("Quit")) {
                //controller.savePlayer();
                System.exit(0);
            }  //end else if
        }
    }

}  //class GUI     
