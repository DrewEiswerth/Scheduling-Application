package org.SchedulingApplication.Utilities;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class StageSetter {

    public static Stage setStage(ActionEvent event, String location, String title) throws IOException {

        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();  // gets the current stage
        Parent scene = FXMLLoader.load(StageSetter.class.getResource(location));    // loads the new scene
        stage.setScene(new Scene(scene));                                           // sets new scene in current stage
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.setTitle(title);

        return stage;
    }
}
