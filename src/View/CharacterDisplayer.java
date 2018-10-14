package View;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class CharacterDisplayer extends Canvas {

    private int characterRowPosition=-1;
    private int characterColPosition=-1;
    private int mazeRowLength;
    private int mazeColLength;
    private GraphicsContext gc;
    private StringProperty imageFileNameCharacter = new SimpleStringProperty();

    /**
     * save maze dimensions for drawing
     * @param rows
     * @param cols
     */
    public void initializeSize(int rows,int cols) {
        mazeRowLength=rows;
        mazeColLength=cols;
        gc = getGraphicsContext2D();
    }

    /**
     * sets character on screen when moved and deleted old one
     * @param rowPosition
     * @param colPosition
     */
    public void setCharacter(int rowPosition, int colPosition) {
        try {
            Image characterImage = new Image(new FileInputStream(getImageFileNameCharacter()));
            if (characterRowPosition != -1 || characterColPosition != -1)
                gc.clearRect(0, 0, getWidth(), getHeight());
            characterRowPosition = rowPosition;
            characterColPosition = colPosition;
            double cellHeight = getHeight() / mazeRowLength;
            double cellWidth = getWidth() / mazeColLength;
            gc.drawImage(characterImage,characterColPosition*cellWidth, characterRowPosition*cellHeight,cellWidth,cellHeight);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getImageFileNameCharacter() {
        return imageFileNameCharacter.get();
    }

    public void setImageFileNameCharacter(String imageFileNameCharacter) {
        this.imageFileNameCharacter.set(imageFileNameCharacter);
    }


}
