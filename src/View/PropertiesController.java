package View;

import Server.Configurations;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

/**
 * Properties screen to decide which maze will be created and how it will be solved
 */
public class PropertiesController {

    public ChoiceBox levelBox;
    public ChoiceBox solveBox;
    public Button btn_submitProperties;
    public Button btn_cancelProperties;

    private ObservableList<String> levelList = FXCollections.observableArrayList("Very Very Easy", "Hard");
    private ObservableList<String> solveList = FXCollections.observableArrayList("BFS", "DFS","BestFS");

    /**
     * Initialize choice boxes
     */
    @FXML
    public void initialize(){
        levelBox.setValue("Hard");
        levelBox.setItems(levelList);
        solveBox.setValue("BFS");
        solveBox.setItems(solveList);
    }

    /**
     * catch property changed and put inside config.properties file
     * @param actionEvent
     */
    public void changeProperties(ActionEvent actionEvent) {
        String level = (String)levelBox.getValue();
        String algorithm = (String)solveBox.getValue();
        Configurations.setProperties(level,algorithm);
        Stage stage = (Stage) btn_submitProperties.getScene().getWindow();
        stage.close();
    }

    /**
     * leave the properties screen
     * @param actionEvent
     */
    public void exitProperties(ActionEvent actionEvent) {
        Stage stage = (Stage) btn_cancelProperties.getScene().getWindow();
        stage.close();
    }
}