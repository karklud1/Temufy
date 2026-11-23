package com.example.temufy;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("TemufyView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 460, 500);
        stage.setTitle("Temufy");
        stage.setScene(scene);
        stage.show();
    }
}
