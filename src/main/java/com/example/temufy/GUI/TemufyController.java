package com.example.temufy.GUI;

import com.example.temufy.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class TemufyController {

    public void handleClickCreate(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("AddNew.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 520, 400);

        Stage stage = new Stage();
        stage.setTitle("Add Song");
        stage.setScene(scene);
        stage.show();
    }

}
