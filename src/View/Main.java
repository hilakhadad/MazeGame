package View;

import Model.Model;
import ViewModel.MyViewModel;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.util.Optional;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Model model = new Model();
        model.startServers();
        MyViewModel viewModel = new MyViewModel(model);
        model.addObserver(viewModel);
        //--------------
        primaryStage.setTitle("My Application!");
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource("MyView.fxml").openStream());
        Scene scene = new Scene(root, 800, 630);
        scene.getStylesheets().add(getClass().getResource("MyViewStyle.css").toExternalForm());
        primaryStage.setScene(scene);
        //--------------
        MyViewController view = fxmlLoader.getController();
        view.setViewModel(viewModel);
        view.setResizeEvent(scene);
        view.turnOnAndOfSound();
        view.changeImages();
        view.setMaximizedEvent(primaryStage);
        viewModel.addObserver(view);
        //--------------
        SetStageCloseEvent(primaryStage, model);
        primaryStage.show();
    }

    private void SetStageCloseEvent(Stage primaryStage, Model model) {
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>()
        {
            public void handle(WindowEvent windowEvent) {
                ButtonType leave = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
                ButtonType stay = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Are you sure you want to exit?",leave,stay);
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == leave) {
                    model.stopServers();
                } else {
                    windowEvent.consume();
                }
            }
        });
    }

}