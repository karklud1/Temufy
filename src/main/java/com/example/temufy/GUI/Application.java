package com.example.temufy.GUI;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("/com/example/temufy/temufyview.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 634);
        stage.setTitle("Temufy");
        stage.setScene(scene);
        stage.show();
    }
}
