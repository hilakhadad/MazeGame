package View;

import algorithms.mazeGenerators.Maze;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class MazeDisplayer extends Canvas {

    private Maze maze;
    private StringProperty imageFileNameWall = new SimpleStringProperty();
    private StringProperty imageFileNameGoal = new SimpleStringProperty();

    /**
     * sets new maze to the displayer
     * @param maze
     */
    public void setMaze(Maze maze) {
        this.maze = maze;
        redrawMaze();
    }

    /**
     * draws the new maze to the screen
     */
    private void redrawMaze() {
        if (maze != null) {
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            double cellHeight = canvasHeight / maze.getRowLength();
            double cellWidth = canvasWidth / maze.getColLength();
            try {
                Image wallImage = new Image(new FileInputStream(getImageFileNameWall()));
                Image goalImage = new Image(new FileInputStream(getImageFileNameGoal()));
                GraphicsContext gc = getGraphicsContext2D();
                gc.clearRect(0, 0, getWidth(), getHeight());
                //draw all the maze cells on board
                for (int i = 0; i < maze.getRowLength(); i++) {
                    for (int j = 0; j < maze.getColLength(); j++) {
                        if (!maze.getStatus(i, j)) { // if cell is a wall draw wall image
                            gc.drawImage(wallImage,j*cellWidth, i*cellHeight,cellWidth,cellHeight);
                        }
                        if (i==maze.getGoalPosition().getRowIndex() && j==maze.getGoalPosition().getColumnIndex())//draw goal image
                            gc.drawImage(goalImage,j*cellWidth, i*cellHeight,cellWidth,cellHeight);
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public String getImageFileNameWall() {
        return imageFileNameWall.get();
    }

    public void setImageFileNameWall(String imageFileNameWall) {
        this.imageFileNameWall.set(imageFileNameWall);
    }

    public String getImageFileNameGoal() {
        return imageFileNameGoal.get();
    }

    public void setImageFileNameGoal(String imageFileNameGoal) {
        this.imageFileNameGoal.set(imageFileNameGoal);
    }
}