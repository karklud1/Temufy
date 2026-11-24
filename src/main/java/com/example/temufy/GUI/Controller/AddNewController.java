package com.example.temufy.GUI.Controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class AddNewController {
    //Java variables
    private String title;
    private String artist;
    private String time;
    private String genre;

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
        title = txtTitle.getText();
        artist = txtArtist.getText();
        time = txtTime.getText();
        genre = cboxGenre.getValue();
        System.out.println("The title of the song is " + title + "\nThe artist is " + artist + "\nThe song is " + time + " minutes long" + "\nThe genre is " + genre);
    }
    public String getTitle(){
        return title;
    }
    public String getArtist(){
        return artist;
    }
    public String getTime(){
        return time;
    }
    public String getGenre(){
        return genre;
    }
}
