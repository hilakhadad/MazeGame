package View;

import algorithms.search.AState;
import algorithms.search.MazeState;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class SolutionDisplayer extends Canvas {
    private Solution solution;
    private int mazeRowLength;
    private int mazeColLength;
    private StringProperty imageFileNameSolution = new SimpleStringProperty();
    private GraphicsContext gc;

    /**
     * initialize maze dimensions
     * @param rows
     * @param cols
     */
    public void initializeSize(int rows,int cols) {
        mazeRowLength=rows;
        mazeColLength=cols;
        gc = getGraphicsContext2D();
    }

    /**
     * draw the solution
     * @param sol
     */
    public void setSolution(Solution sol) {
        //go throw the solution and draw all the path with solution image
        this.solution = sol;
        gc.clearRect(0,0,getWidth(),getHeight());
        try {
            Image solImage = new Image(new FileInputStream(getImageFileNameSolution()));
            ArrayList<AState> solList = solution.getSolutionPath();
            double cellHeight = getHeight() / mazeRowLength;
            double cellWidth = getWidth() / mazeColLength;
            for (int i = 0; i < solList.size()-1; i++) {
                MazeState draw = (MazeState) solList.get(i);
                gc.drawImage(solImage,draw.getPosition().getColumnIndex()*cellWidth,draw.getPosition().getRowIndex()*cellHeight,cellWidth,cellHeight);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getImageFileNameSolution() {
        return imageFileNameSolution.get();
    }

    public void setImageFileNameSolution(String imageFileNameSolution) {
        this.imageFileNameSolution.set(imageFileNameSolution);
    }

    public void clearSolution() {
        gc.clearRect(0,0,getWidth(),getHeight());
    }
}
