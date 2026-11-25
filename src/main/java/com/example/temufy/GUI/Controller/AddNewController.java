package com.example.temufy.GUI.Controller;

import com.example.temufy.BE.Song;
import com.example.temufy.GUI.Model.SongModel;
import com.example.temufy.GUI.Model.SongTime;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class AddNewController {

    private File selectedFile;

    // Keep a reference to the model (same instance as the main window)
    private SongModel songModel;

    @FXML
    private TextField txtTitle, txtArtist, txtTime, txtFile;
    @FXML
    private ChoiceBox<String> cboxGenre;
    @FXML
    private Button btnFile, btnCancel, btnSave;

    /**
     * Called by TemufyController right after the window is loaded
     */
    public void setModel(SongModel songModel) {
        this.songModel = songModel;
    }

    @FXML
    public void initialize() {
        cboxGenre.getItems().addAll("POP", "Rock", "RnB & Soul", "Hip-Hop & Rap", "Electronic & Dance",
                "Classical", "Country", "Reggae", "Jazz", "Blues", "Folk", "Latin");
        cboxGenre.setValue("Genre...");
    }

    /**
     *
     */
    @FXML
    public void handleClickSave(ActionEvent actionEvent) {
        try {
            // get user song data from UI
            String title = txtTitle.getText();
            String artist = txtArtist.getText();
            String genre = cboxGenre.getValue();
            double time = parseTime(txtTime.getText());

            if (selectedFile == null) {
                throw new Exception("Please choose an audio file.");
            }

            // new song object (the database creates the id)
            Song newSong = new Song(title, artist, genre, time, selectedFile.getAbsolutePath(), 0);

            // call model to create the song in the dal
            songModel.createSong(newSong);

            // close the window
            Stage stage = (Stage) btnSave.getScene().getWindow();
            stage.close();
        }
        catch (NumberFormatException e) {
            displayError(new Exception("Time must be like 3:30 or 3.5"));
        }
        catch (Exception e) {
            displayError(e);
        }
    }

    @FXML
    public void handleClickCancel(ActionEvent actionEvent) {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
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
        File chosen = fileChooser.showOpenDialog(stage);

        if (chosen != null) {
            selectedFile = chosen;
            txtFile.setText(selectedFile.getName());
            songTime.songTime(selectedFile, txtTime, txtTitle);
        }
    }

    /**
     * "3:45" (what SongTime writes) or "3.5" -> minutes
     */
    private double parseTime(String text) {
        text = text.trim().replace(',', '.');

        if (text.contains(":")) {
            String[] parts = text.split(":");
            return Integer.parseInt(parts[0].trim()) + Integer.parseInt(parts[1].trim()) / 60.0;
        }
        return Double.parseDouble(text);
    }

    private void displayError(Throwable t) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Something went wrong");
        alert.setHeaderText(t.getMessage());
        alert.showAndWait();
    }
}