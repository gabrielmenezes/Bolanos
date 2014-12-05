/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connectfourchampionship;

/**
 *
 * @author MarcioNote
 */
public interface IPlayer {

    public int getNextMove(int[][] gameBoard);
    
    public String getTeamName();
}
