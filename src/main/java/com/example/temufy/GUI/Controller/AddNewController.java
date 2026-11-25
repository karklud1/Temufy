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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AddNewController {

    // Folder in the project root where the audio files are kept
    private static final String MP3_FOLDER = "mp3";

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
     * Same idea as onCreate in MovieViewController
     */
    @FXML
    public void handleClickSave(ActionEvent actionEvent) {
        try {
            // get user song data from UI
            String title = txtTitle.getText().trim();
            String artist = txtArtist.getText().trim();
            String genre = cboxGenre.getValue();
            double time = parseTime(txtTime.getText());

            if (title.isEmpty() || artist.isEmpty()) {
                throw new Exception("Please enter a title and an artist.");
            }
            if (selectedFile == null) {
                throw new Exception("Please choose an audio file.");
            }
            if (genre == null || genre.equals("Genre...")) {
                genre = null;   // Genre is optional in the database
            }

            // copy the file into the mp3 folder, store only the relative path (mp3/song.mp3)
            String relativePath = copyToMp3Folder(selectedFile);

            // new song object (id = 0, the database creates the real id)
            Song newSong = new Song(title, artist, genre, time, relativePath, 0);

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
     * Copies the chosen audio file into the project's mp3 folder
     * and returns the relative path that is stored in the database (e.g. "mp3/song.mp3").
     */
    private String copyToMp3Folder(File source) throws IOException {
        Path mp3Dir = Paths.get(MP3_FOLDER).toAbsolutePath().normalize();
        Files.createDirectories(mp3Dir);

        Path src = source.toPath().toAbsolutePath().normalize();

        // Already inside the mp3 folder: nothing to copy
        if (src.startsWith(mp3Dir)) {
            return MP3_FOLDER + "/" + mp3Dir.relativize(src).toString().replace('\\', '/');
        }

        // Do not overwrite a different file with the same name: song.mp3 -> song (1).mp3
        String fileName = source.getName();
        int dot = fileName.lastIndexOf('.');
        String baseName = dot > 0 ? fileName.substring(0, dot) : fileName;
        String extension = dot > 0 ? fileName.substring(dot) : "";

        Path target = mp3Dir.resolve(fileName);
        int counter = 1;
        while (Files.exists(target)) {
            target = mp3Dir.resolve(baseName + " (" + counter + ")" + extension);
            counter++;
        }

        Files.copy(src, target);
        return MP3_FOLDER + "/" + target.getFileName();
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