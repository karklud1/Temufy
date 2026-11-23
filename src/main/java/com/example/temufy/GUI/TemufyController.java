package com.example.temufy.GUI;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class TemufyController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}
