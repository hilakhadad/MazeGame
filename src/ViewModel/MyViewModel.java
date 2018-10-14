package ViewModel;

import Model.IModel;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.input.KeyCode;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

/**
 * Mediator between Model and View (sends all view requests to the model and reads when model raises flag)
 */
public class MyViewModel extends Observable implements Observer {

    private IModel model;
    private int characterPositionRowIndex;
    private int characterPositionColumnIndex;
    public StringProperty characterPositionRow;
    public StringProperty characterPositionColumn;

    public MyViewModel(IModel model) {
        if (model != null) {
            this.model = model;
            characterPositionRow = new SimpleStringProperty(""+this.model.getCharacterPositionRow());
            characterPositionColumn = new SimpleStringProperty(""+this.model.getCharacterPositionColumn());
        }
    }

    @Override
    public void update(Observable o, Object arg) { //Observer method when flag is raised
        if (o==model){
            characterPositionRowIndex = model.getCharacterPositionRow();
            characterPositionRow.set(characterPositionRowIndex + "");
            characterPositionColumnIndex = model.getCharacterPositionColumn();
            characterPositionColumn.set(characterPositionColumnIndex + "");
            setChanged();
            notifyObservers(arg);
        }
    }

    public void generateMaze(int width, int height){
        model.generateMaze(width, height);
    }

    public void moveCharacter(KeyCode movement){
        model.moveCharacter(movement);
    }

    public Maze getMaze() {
        return model.getMaze();
    }

    public int getCharacterPositionRow() {
        return characterPositionRowIndex;
    }

    public int getCharacterPositionColumn() {
        return characterPositionColumnIndex;
    }

    public  void solveMaze(){ model.solveMaze();}

    public Solution getSolution(){ return model.getSolution();}

    public void closeServer(){model.stopServers();}

    public Maze loadMaze(File chosen){
        return model.loadMaze(chosen);
    }

    public void saveMaze(File chosen) {
        model.saveMaze(chosen);
    }
}