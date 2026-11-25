package com.example.temufy.GUI.Controller;

import com.example.temufy.BE.Playlist;
import com.example.temufy.BE.Song;
import com.example.temufy.GUI.Application;
import com.example.temufy.GUI.Model.SongModel;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TemufyController implements Initializable {

    //FXML variables
    @FXML
    private TextField txtSongSearch;
    @FXML
    private Button btnSongCreate, btnSongDelete, btnPlaylistNew, btnPlaylistEdit, btnPlaylistDelete;
    @FXML
    private TableView<Song> tblSong;
    @FXML
    private TableView<Playlist> tblPlaylist;
    @FXML
    private TableColumn<Song, String> colSongtitle, colSongArtist, colSongGenre, colSongTime;
    @FXML
    private TableColumn<Playlist, String> colPlaylistName, colPlaylistSongs, colPlaylistTime;
    @FXML
    private Slider volumeSlider;

    private SongModel songModel;
    private MediaPlayer mediaPlayer;

    public TemufyController() {
        try {
            songModel = new SongModel();
        } catch (Exception e) {
            displayError(e);
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        colSongtitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colSongArtist.setCellValueFactory(new PropertyValueFactory<>("artist"));
        colSongGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        colSongTime.setCellValueFactory(new PropertyValueFactory<>("time"));

        txtSongSearch.textProperty().addListener((observableValue, oldValue, newValue) -> {
            songModel.getObservableSongs().setPredicate(song -> {

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (song.getTitle().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (song.getArtist().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else return song.getGenre().toLowerCase().contains(lowerCaseFilter);
            });
        });

        SortedList<Song> sortedData = new SortedList<>(songModel.getObservableSongs());
        sortedData.comparatorProperty().bind(tblSong.comparatorProperty());
        tblSong.setItems(sortedData);

        volumeSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(newValue.doubleValue());
            }
        });

        tblSong.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Song selected = tblSong.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    playSong(selected);
                }
            }
        });
    }

    private void playSong(Song song) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }
        try {
            Media media = new Media(new File(song.getFilePath()).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setVolume(volumeSlider.getValue()); // nye players starter på 1.0
            mediaPlayer.play();
        } catch (Exception e) {
            displayError(e);
        }
    }

    private void displayError(Throwable t) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Something went wrong");
        alert.setHeaderText(t.getMessage());
        alert.showAndWait();
    }

    @FXML
    private void handleClickCreate(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("/com/example/temufy/AddNew.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 520, 400);

        // Send den samme model med til AddNew-controlleren, så tabellen opdateres
        AddNewController controller = fxmlLoader.getController();
        controller.setModel(songModel);

        Stage stage = new Stage();
        stage.setTitle("Add Song");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void handleClickDelete(ActionEvent actionEvent) throws Exception {
        Song selectedSong = tblSong.getSelectionModel().getSelectedItem();

        if (selectedSong != null) {
            songModel.deleteSong(selectedSong);
        }
    }
}