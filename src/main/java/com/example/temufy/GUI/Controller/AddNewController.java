package com.example.temufy.GUI.Controller;
import com.example.temufy.BE.Song;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class AddNewController {

    Song song;

    //FXML variables
    @FXML
    private TextField txtTitle;
    @FXML
    private TextField txtArtist;
    @FXML
    private ChoiceBox<String> cboxGenre;
    @FXML
    private TextField txtTime;
    @FXML
    private TextField txtFile;
    @FXML
    private Button btnFile;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnSave;

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
    @FXML
    public void handleClickSave(ActionEvent actionEvent){
        txtTitle.getText();
        txtArtist.getText();
        Double.parseDouble(txtTime.getText());
        cboxGenre.getValue();
        System.out.print("\nThe title of the song is " + song.getTitle() + "\nThe artist is " + song.getArtist() + "\nThe song is " + song.getTime() + " minutes long" + "\nThe genre is " + song.getGenre());
    }
}
