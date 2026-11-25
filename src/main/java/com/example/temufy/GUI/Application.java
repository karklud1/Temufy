package com.example.temufy.GUI;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("/com/example/temufy/TemufyView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 757);
        stage.setTitle("Temufy");
        stage.setScene(scene);
        stage.show();
    }
}
