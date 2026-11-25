package com.example.temufy.GUI.Controller;

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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TemufyController implements Initializable {
    //FXML variables
    @FXML
    private TextField txtSongSearch;
    @FXML
    private Button btnSongCreate,btnSongDelete,btnPlaylistNew,btnPlaylistEdit,btnPlaylistDelete;
    @FXML
    private TableView<Song> tblSong,tblPlaylist;
    @FXML
    private TableColumn<Song, String> colSongtitle,colSongArtist,colSongGenre,colSongTime,colPlaylistName,colPlaylistSongs,colPlaylistTime;

    private SongModel songModel;

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
        // Opsæt kolonner i tableview
        colSongtitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colSongArtist.setCellValueFactory(new PropertyValueFactory<>("artist"));
        colSongGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        colSongTime.setCellValueFactory(new PropertyValueFactory<>("time"));

        // Lyt til søgefeltet (filtrerer FilteredList'en i modellen)
        txtSongSearch.textProperty().addListener((observableValue, oldValue, newValue) -> {
            songModel.getObservableSongs().setPredicate(song -> {

                // Hvis søgefeltet er tomt, vis alle sange
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

        // Gør tabellen sorterbar ved klik på kolonneoverskrifter
        SortedList<Song> sortedData = new SortedList<>(songModel.getObservableSongs());
        sortedData.comparatorProperty().bind(tblSong.comparatorProperty());
        tblSong.setItems(sortedData);
    }

    private void displayError(Throwable t) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Something went wrong");
        alert.setHeaderText(t.getMessage());
        alert.showAndWait();
    }

    /**
     * Åbner "Add Song"-vinduet
     */
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

    /**
     * Sletter den valgte sang
     */
    @FXML
    private void handleClickDelete(ActionEvent actionEvent) throws Exception {
        Song selectedSong = tblSong.getSelectionModel().getSelectedItem();

        if (selectedSong != null) {
            songModel.deleteSong(selectedSong);
        }
    }
}