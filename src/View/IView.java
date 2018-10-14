package View;

import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;

public interface IView {

    /**
     * display maze on screen
     * @param maze
     */
    void displayMaze (Maze maze);

    /**
     * display solution on screen
     * @param sol
     */
    void displaySolution(Solution sol);

    /**
     * display character on screen
     * @param dimensions - dimensions[0] - rows, dimensions[1] - cols
     */
    void displayCharacter(int[] dimensions);
}