package com.example.temufy.GUI;
import com.example.temufy.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class AddNewController {
    @FXML
    TextField txtTitle;
    @FXML
    TextField txtArtist;
    @FXML
    ChoiceBox<String> cboxGenre;
    @FXML
    TextField txtTime;
    @FXML
    TextField txtFile;
    @FXML
    Button btnFile;
    @FXML
    Button btnCancel;
    @FXML
    Button btnSave;

    @FXML
    public void initialize(){
        cboxGenre.getItems().addAll("POP","Rock","RnB & Soul","Hip-Hop & Rap","Electronic & Dance","Classical","Country","Reggae","Jazz", "Blues","Folk","Latin");
        cboxGenre.setValue("Genre...");
    }
    @FXML
    public void handleClickCancel(ActionEvent actionEvent){
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
}
