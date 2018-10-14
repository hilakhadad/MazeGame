package View;

import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.*;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;
import java.util.Random;

/**
 * incharge of the view, makes everything appear and disappear
 */
public class MyViewController implements Observer, IView {

    private MyViewModel viewModel;
    private int goalX; // x cord of winning cell
    private int goalY; // y cord of winning cell
    private boolean solutionDisplayed =false; // whether solution should be displayed or not
    private String backGroundSound; // background sound according to the character picked
    private String winningSound = "src/resources/Music/youWon.wav";
    private boolean isMusicWorking = false; //wheter music should be working
    private Media sound;
    private MediaPlayer mediaPlayer;
    private ObservableList<String> gamesList = FXCollections.observableArrayList("Flame", "Icy Tower","Pacman");
    private StringProperty characterPositionRow = new SimpleStringProperty(); // for binding
    private StringProperty characterPositionColumn = new SimpleStringProperty(); // for binding
    @FXML
    public MazeDisplayer mazeDisplayer;
    public CharacterDisplayer characterDisplayer;
    public SolutionDisplayer solutionDisplayer;
    public javafx.scene.control.TextField txtfld_rowsNum;
    public javafx.scene.control.TextField txtfld_columnsNum;
    public javafx.scene.control.Label lbl_rowsNum;
    public javafx.scene.control.Label lbl_columnsNum;
    public javafx.scene.control.Button btn_generateMaze;
    public javafx.scene.control.Button btn_solveMaze;
    public javafx.scene.control.Button btn_desolveMaze;
    public RadioButton soundCheck;
    public ChoiceBox gameBox;
    public Pane pane;
    public BorderPane mainBorder;


    /**
     * set the view model and bind character position
     * @param viewModel
     */
    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
        bindProperties(viewModel);
    }

    /**
     * bind the character position
     * @param viewModel
     */
    private void bindProperties(MyViewModel viewModel) {
        lbl_rowsNum.textProperty().bind(viewModel.characterPositionRow);
        lbl_columnsNum.textProperty().bind(viewModel.characterPositionColumn);
    }

    /**
     * Obersver of viewmodel when it raises flag something has changed
     * @param o
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {
        if (soundCheck.isSelected())
            stopMusic();
        if (o == viewModel && arg instanceof Maze) {//maze returned
            btn_generateMaze.setDisable(false);
            btn_solveMaze.setVisible(true);
            btn_desolveMaze.setVisible(false);
            displayMaze((Maze) arg);
        }
        if (o == viewModel && arg instanceof int[]) {//character movement returned
            displayCharacter((int[]) arg);
        }
        if (o == viewModel && arg instanceof Solution) {//solution returned
            displaySolution((Solution) arg);
            btn_solveMaze.setVisible(false);
            btn_desolveMaze.setVisible(true);
            solutionDisplayed =true;
        }
    }

    /**
     * display the maze on the window
     * @param maze
     */
    @Override
    public void displayMaze(Maze maze) {
        setWidthAndHeight(pane);//update displayers with and height
        mazeDisplayer.setMaze(maze);
        //initialize sizes
        characterDisplayer.initializeSize(maze.getRowLength(), maze.getColLength());
        solutionDisplayer.initializeSize(maze.getRowLength(), maze.getColLength());
        //display character on screen with start position of maze
        displayCharacter(new int[]{maze.getStartPosition().getRowIndex(), maze.getStartPosition().getColumnIndex()});
        btn_generateMaze.setDisable(false);
        //for binding
        int characterPositionRow = viewModel.getCharacterPositionRow();
        int characterPositionColumn = viewModel.getCharacterPositionColumn();
        goalX = maze.getGoalPosition().getRowIndex();
        goalY = maze.getGoalPosition().getColumnIndex();
        this.characterPositionRow.set(characterPositionRow + "");
        this.characterPositionColumn.set(characterPositionColumn + "");
    }

    /**
     * displays the character on screen
     * @param arg
     */
    @Override
    public void displayCharacter(int[] arg) {
        characterDisplayer.setCharacter(arg[0], arg[1]);
        //if goal position was reached
        if (arg[0] == goalX && arg[1] == goalY){
            stopMusic();
            startMusic(winningSound,false);
            ButtonType stay = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
            ButtonType leave = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Wanna play another one?",leave,stay);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == leave) {
                exit();
                return;
            }
            else if (result.get() == stay){
                generateMaze();
                return;
            }

        }
        this.characterPositionRow.set(characterPositionRow + "");
        this.characterPositionColumn.set(characterPositionColumn + "");
    }

    /**
     * displays the solution on screen
     * @param arg
     */
    @Override
    public void displaySolution(Solution arg) {
        solutionDisplayer.setSolution(arg);
    }

    /**
     * asks view model for new maze
     */
    public void generateMaze() {
        changeGame();
        String rowsNum = txtfld_rowsNum.getText();
        String colsNum = txtfld_columnsNum.getText();
        if (!isNumber(rowsNum) || !isNumber(colsNum)) {
            showAlert("Invalid maze measures!");
            return;
        }
        stopMusic();
        int height = Integer.valueOf(txtfld_rowsNum.getText());
        int width = Integer.valueOf(txtfld_columnsNum.getText());
        if(height < 4 || width < 4) {
            showAlert("Height and width too small, 4*4 maze was created!");
            height = width = 4;
        }
        if(solutionDisplayed)
            desolveMaze();
        btn_generateMaze.setDisable(true);
        viewModel.generateMaze(width, height);
        startMusic(backGroundSound,true);
    }

    /**
     * checks if txt field parameters are legal
     * @param num
     * @return
     */
    private boolean isNumber(String num) {
        if (num.length() == 0)
            return false;
        for (int i = 0; i < num.length(); i++) {
            if( num.charAt(i) < '0' || num.charAt(i) > '9')
                return false;
        }
        return true;
    }

    /**
     * asks view model for solution
     */
    public void solveMaze() {
        viewModel.solveMaze();
    }

    /**
     * earses maze from window
     */
    public void desolveMaze() {
        solutionDisplayer.clearSolution();
        btn_desolveMaze.setVisible(false);
        btn_solveMaze.setVisible(true);
        solutionDisplayed = false;
    }

    /**
     * shows alert with given string
     * @param alertMessage
     */
    private void showAlert(String alertMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(alertMessage);
        alert.show();
    }

    /**
     * catches key pressed for character moving
     * @param keyEvent
     */
    public void KeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode().isKeypadKey())
            viewModel.moveCharacter(keyEvent.getCode());
        keyEvent.consume();
    }

    /**
     * resize window
     * @param scene
     */
    public void setResizeEvent(Scene scene) {
        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                setWidthAndHeight(pane);
                displayWhenResize();
            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                setWidthAndHeight(pane);
                displayWhenResize();
            }
        });
    }

    /**
     * displays all the displayers when resize is done
     */
    public void displayWhenResize() {
        Maze maze = viewModel.getMaze();
        if(maze!=null) {
            mazeDisplayer.setMaze(viewModel.getMaze());
            characterDisplayer.setCharacter(viewModel.getCharacterPositionRow(), viewModel.getCharacterPositionColumn());
            if (solutionDisplayed)
                solutionDisplayer.setSolution(viewModel.getSolution());
        }
    }

    /**
     * saves the current maze to file
     * @param actionEvent
     * @throws IOException
     */
    public void savingMaze(ActionEvent actionEvent) throws IOException {
        Maze toSave = viewModel.getMaze();
        if (toSave == null) {
            showAlert("No maze to Save");
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Maze");
        File defaultDirectory = new File("src/resources/Mazes");
        fileChooser.setInitialDirectory(defaultDirectory);
        Random rnd = new Random();
        fileChooser.setInitialFileName("myBeatifulMaze" + rnd.nextInt(1000));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MAZE files (.maze)", ".maze"));
        File chosen = fileChooser.showSaveDialog(new Stage());
        if (chosen != null) {
            viewModel.saveMaze(chosen);
        }
    }

    /**
     * loades a maze from the a file
     * @param actionEvent
     */
    public void loadingMaze(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Maze");
        File defaultDirectory = new File("src/resources/Mazes");
        fileChooser.setInitialDirectory(defaultDirectory);
        File chosen = fileChooser.showOpenDialog(new Stage());
        if (chosen != null) {
            Maze maze = viewModel.loadMaze(chosen);
            if(maze!=null) {
                btn_generateMaze.setDisable(false);
                btn_solveMaze.setVisible(true);
                btn_desolveMaze.setVisible(false);
                if (solutionDisplayed)
                    solutionDisplayer.clearSolution();
                changeGame();
                if (isMusicWorking)
                    stopMusic();
                startMusic(backGroundSound, true);
                displayMaze(maze);
                txtfld_columnsNum.setFocusTraversable(false);
                txtfld_rowsNum.setFocusTraversable(false);
            }
            else
                showAlert("Not a maze!");
        }
    }

    /**
     * set the properties in properties windown
     * @param actionEvent
     */
    public void setProperties(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            stage.setTitle("Properties");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("Properties.fxml").openStream());
            Scene scene = new Scene(root, 400, 200);
            scene.getStylesheets().add(getClass().getResource("MyPropertiesStyle.css").toExternalForm());
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
        } catch (Exception e) {

        }
    }

    /**
     * start the music according to character chosen
     * @param musicToPlay
     * @param repeat
     */
    private void startMusic(String musicToPlay, boolean repeat){
        if (!soundCheck.isSelected()) {
            isMusicWorking = true;
            String s = new File(musicToPlay).toURI().toString();
            sound = new Media(s);
            mediaPlayer = new MediaPlayer(sound);
            if (repeat) mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.play();
        }
    }

    /**
     * stop the music
     */
    private void stopMusic(){
        if (isMusicWorking){
            isMusicWorking =false;
            mediaPlayer.stop();
        }
    }

    /**
     * opens about screen
     * @param actionEvent
     */
    public void about(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            stage.setTitle("About");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("MyAbout.fxml").openStream());
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
        } catch (Exception e) {

        }
    }

    /**
     * opens help screen
     * @param actionEvent
     */
    public void help(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            stage.setTitle("Help");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("MyHelp.fxml").openStream());
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
        } catch (Exception e) {

        }
    }

    /**
     * initializes choice box
     */
    public void initialize() {
        gameBox.setValue("Icy Tower");
        gameBox.setItems(gamesList);

    }

    /**
     * changes character and music
     */
    private void changeGame() {
        String game = (String)gameBox.getValue();
        int i=1;
        if (game.equals("Pacman"))
            i=3;

        if (game.equals("Icy Tower"))
            i=2;

        mazeDisplayer.setImageFileNameGoal("src/resources/Images/goal"+i+".jpg");
        mazeDisplayer.setImageFileNameWall("src/resources/Images/wall"+i+".jpg");
        solutionDisplayer.setImageFileNameSolution("src/resources/Images/path"+i+".jpg");
        characterDisplayer.setImageFileNameCharacter("src/resources/Images/character"+i+".jpg");
        setBackGroundSound("src/resources/Music/backGround"+i+".wav");
    }

    /**
     * leave the game
     */
    public void exit(){
        viewModel.closeServer();
        Stage close = (Stage)btn_solveMaze.getScene().getWindow();
        close.close();
    }

    /**
     * catches stop/start music button
     */
    public void turnOnAndOfSound() {
        soundCheck.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue)
                    stopMusic();
                if (oldValue)
                    startMusic(backGroundSound,true);
            }
        });
    }

    /**
     * set background music
     * @param backGroundSound
     */
    private void setBackGroundSound(String backGroundSound) {
        this.backGroundSound = backGroundSound;
    }

    /**
     * catch selection changed in character options
     */
    public void changeImages() {

        gameBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                changeGame();
                Maze maze = viewModel.getMaze();
                if(maze!=null) {
                    displayMaze(maze);
                    displayCharacter(new int[]{viewModel.getCharacterPositionRow(),viewModel.getCharacterPositionColumn()});
                    Solution sol = viewModel.getSolution();
                    if(sol!=null && solutionDisplayed)
                        displaySolution(sol);
                    if (isMusicWorking) {
                        stopMusic();
                        startMusic(backGroundSound, true);
                    }
                }
            }
        });
    }

    /**
     * catch zoom event
     * @param scrollEvent
     */
    public void zoomWithWheel(ScrollEvent scrollEvent) {

        if (scrollEvent.isControlDown() && viewModel.getMaze()!=null) {
            pane.setOnScroll(new EventHandler<ScrollEvent>() {
                @Override
                public void handle(ScrollEvent event) {
                    double zoomFactor = 1.05;
                    double deltaY = event.getDeltaY();
                    if (deltaY < 0) {
                        zoomFactor = 2.0 - zoomFactor;
                    }
                    pane.autosize();
                    pane.setPrefWidth(pane.getWidth() * zoomFactor);
                    pane.setPrefHeight(pane.getHeight() * zoomFactor);
                    setWidthAndHeight(pane);
                    displayWhenResize();
                }
            });
        }
        scrollEvent.consume();;

    }

    /**
     * changes the width and height of displayers
     * @param x
     */
    private void setWidthAndHeight(Pane x){
        if(viewModel.getMaze()!=null) {
            double change = x.getWidth();
            if(x.getHeight() < change)
                change = x.getHeight();
            mazeDisplayer.setHeight(change);
            mazeDisplayer.setWidth(change);
            characterDisplayer.setHeight(change);
            characterDisplayer.setWidth(change);
            solutionDisplayer.setHeight(change);
            solutionDisplayer.setWidth(change);
        }

    }

    /**
     * catches window maximized event
     * @param primaryStage
     */
    public void setMaximizedEvent(Stage primaryStage) {
        primaryStage.maximizedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                pane.autosize();
                pane.setPrefWidth(mainBorder.getWidth()-30);
                pane.setPrefHeight(mainBorder.getHeight()-30);
                if(newValue)
                    primaryStage.setFullScreen(true);

            }
        });
    }
}