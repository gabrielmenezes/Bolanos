package connectfourchampionship;

/**
 * Jason Fletchall Group Members: Mario Giombi, Brian Schuette CS540 Section 1 -
 * Jerry Zhu December 4, 2005
 *
 * Starts up the Connect 4 game with a GUI
 *
 * Usage: java Connect4 time_limit game_type
 *
 * time_limit: maximum time allowed, in milliseconds, for computer player to
 * make each move game_type: "hh" for human vs. human, "hc" for human vs.
 * computer (learning player), "hm" for human vs. minimax player
 */
public class Connect4 {

    public static void main(String[] args) {
        IPlayer p1 = new Bolanos();
        IPlayer p2 = new DummyPlayer();
        Connect4Game.newGame(p1, p2);
    }
}
