package Model;

import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;
import java.io.File;

public interface IModel {

    /**
     * start generation and solving server
     */
    void startServers();
    /**
     * generate maze according to given sizes
     * @param width
     * @param height
     */
    void generateMaze(int width, int height);

    /**
     * solves the current maze
     */
    void solveMaze ();

    /**
     * moves character according to key pressed
     * @param movement
     */
    void moveCharacter(KeyCode movement);

    /**
     * returns the solution when server generated it
     * @return
     */
    Solution getSolution();

    /**
     * returns the maze when the server generated it
     * @return
     */
    Maze getMaze();

    /**
     * returns character row
     * @return
     */
    int getCharacterPositionRow();

    /**
     * returns character col
     * @return
     */
    int getCharacterPositionColumn();

    /**
     * updates the maze
     * @param maze
     */
    void setMaze(Maze maze);

    /**
     * stops the servers
     */
    void stopServers();

    /**
     * sets character when loading maze
     * @param rowIndex
     * @param columnIndex
     */
    void setCharacter(int rowIndex, int columnIndex);

    /**
     * loads maze from a given path
     * @param chosen
     * @return
     */
    Maze loadMaze(File chosen);

    /**
     * save the current maze to the path given
     * @param chosen
     */
    void saveMaze(File chosen);
}