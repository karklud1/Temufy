package com.example.temufy.GUI.Controller;

import com.example.temufy.BE.Song;
import com.example.temufy.GUI.Model.SongModel;
import com.example.temufy.GUI.Model.SongTime;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.*;
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
    private Song songToEdit;

    @FXML
    private TextField txtTitle, txtArtist, txtTime, txtFile;
    @FXML
    private ChoiceBox<String> cboxGenre;
    @FXML
    private Button btnFile, btnCancel, btnSave;
    @FXML
    private Label lblSong;

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
            String title = txtTitle.getText().trim();
            String artist = txtArtist.getText().trim();
            String genre = cboxGenre.getValue();

            if (title.isEmpty() || artist.isEmpty()) {
                throw new Exception("Please enter a title and an artist.");
            }
            if (genre == null || genre.equals("Genre...")) {
                genre = null;   // Genre is optional in the database
            }

            if (songToEdit == null) {
                // CREATE: same as before
                double time = parseTime(txtTime.getText());
                if (selectedFile == null) {
                    throw new Exception("Please choose an audio file.");
                }
                String relativePath = copyToMp3Folder(selectedFile);
                Song newSong = new Song(title, artist, genre, time, relativePath, 0);
                songModel.createSong(newSong);
            } else {
                // EDIT: only text fields change, no copying of the mp3
                songToEdit.setTitle(title);
                songToEdit.setArtist(artist);
                songToEdit.setGenre(genre);
                songModel.updateSong(songToEdit);
            }

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

    // Called by the main window when the user presses Edit
    public void setSongToEdit(Song song) {
        this.songToEdit = song;

        lblSong.setText("Edit song");

        txtTitle.setText(song.getTitle());
        txtArtist.setText(song.getArtist());

        // time is stored in seconds, the field shows m:ss
        int totalSeconds = (int) Math.round(song.getTime());
        txtTime.setText(String.format("%d:%02d", totalSeconds / 60, totalSeconds % 60));

        String genre = song.getGenre();
        cboxGenre.setValue(genre == null ? "Genre..." : genre);

        // the file and time stay as they are in edit mode
        txtFile.setText(song.getFilePath());
        txtFile.setDisable(true);
        txtTime.setDisable(true);
        btnFile.setDisable(true);
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

    // "3:45" or "3.5" minutes becomes whole seconds, because the Time column is an int
    private double parseTime(String text) {
        text = text.trim().replace(',', '.');

        if (text.contains(":")) {
            String[] parts = text.split(":");
            int minutes = Integer.parseInt(parts[0].trim());
            int seconds = Integer.parseInt(parts[1].trim());
            return minutes * 60 + seconds;
        }
        return Math.round(Double.parseDouble(text) * 60);
    }

    private void displayError(Throwable t) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Something went wrong");
        alert.setHeaderText(t.getMessage());
        alert.showAndWait();
    }
}