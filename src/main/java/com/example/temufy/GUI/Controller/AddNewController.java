package com.example.temufy.GUI.Controller;
import com.example.temufy.BE.Song;
import com.example.temufy.GUI.Model.SongTime;
import com.mpatric.mp3agic.Mp3File;
import javafx.stage.FileChooser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;


public class AddNewController {
    private File selectedFile;
    private int durationInSeconds;
    Song song;

    //FXML variables
    @FXML
    private TextField txtTitle,txtArtist,txtTime,txtFile;
    @FXML
    private ChoiceBox<String> cboxGenre;
    @FXML
    private Button btnFile,btnCancel,btnSave;

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
    @FXML
    public void handleClickChoose(ActionEvent actionEvent) {
        SongTime songTime = new SongTime();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Audio File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Audio Files (*.mp3, *.wav)", "*.mp3", "*.wav")
        );

        Stage stage = (Stage) btnFile.getScene().getWindow();
        selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            txtFile.setText(selectedFile.getName());
            songTime.songTime(selectedFile, txtTime, txtTitle);
        }
    }
}
