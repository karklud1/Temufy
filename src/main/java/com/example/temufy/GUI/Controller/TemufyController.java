
package com.example.temufy.GUI.Controller;

import com.example.temufy.BE.Playlist;
import com.example.temufy.BE.Song;
import com.example.temufy.GUI.Application;
import com.example.temufy.GUI.Model.PlaylistModel;
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

    // Playlistens sangtabel
    @FXML
    private TableView<Song> tblSongsInPlayllist;

    @FXML
    private TableColumn<Song, String> colSongsInPlayllisttitle;

    @FXML
    private TableColumn<Song, String> colSongsInPlayllistArtist;

    @FXML
    private TableColumn<Song, String> colSongsInPlayllistGenre;

    @FXML
    private TableColumn<Song, Double> colSongsInPlayllistTime;

    @FXML
    private Button btncolSongsInPlayllistAdd;

    @FXML
    private Button btncolSongsInPlayllistDelete;

    @FXML
    private Button btnSongsNew;

    @FXML
    private Button btnSongsEdit;

    @FXML
    private Button btnSongsDelete;

    // FXML variables
    @FXML
    private TextField txtSongSearch;

    @FXML
    private Button btnSongCreate, btnSongDelete,
            btnPlaylistNew, btnPlaylistEdit,
            btnPlaylistDelete, btnSearch, btnClear;

    @FXML
    private TableView<Song> tblSong;

    @FXML
    private TableView<Playlist> tblPlaylist;

    @FXML
    private TableColumn<Song, String> colSongtitle,
            colSongArtist, colSongGenre;

    @FXML
    private TableColumn<Song, Double> colSongTime;

    @FXML
    private TableColumn<Playlist, String> colPlaylistName;

    @FXML
    private TableColumn<Playlist, Integer> colPlaylistSongs;

    @FXML
    private TableColumn<Playlist, Double> colPlaylistTime;

    private PlaylistModel playlistModel;
    private SongModel songModel;

    public TemufyController() {
        try {
            songModel = new SongModel();
            playlistModel = new PlaylistModel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        songTableShow();
        playlistTableShow();
        playlistSongsTableShow();

        searchSong();

        // Når brugeren vælger en playlist, vises dens sange
        tblPlaylist.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldPlaylist, newPlaylist) -> {

                    if (newPlaylist != null) {
                        showSongsInPlaylist(newPlaylist);
                    } else {
                        tblSongsInPlayllist.getItems().clear();
                    }
                });
    }

    // Søgefunktion til sanglisten
    private void searchSong() {

        txtSongSearch.setDisable(true);

        txtSongSearch.textProperty().addListener(
                (observableValue, oldValue, newValue) -> {

                    songModel.getObservableSongs().setPredicate(song -> {

                        if (newValue == null || newValue.isEmpty()) {
                            return true;
                        }

                        String lowerCaseFilter = newValue.toLowerCase();

                        if (song.getTitle().toLowerCase()
                                .contains(lowerCaseFilter)) {
                            return true;
                        } else if (song.getArtist().toLowerCase()
                                .contains(lowerCaseFilter)) {
                            return true;
                        } else {
                            return song.getGenre().toLowerCase()
                                    .contains(lowerCaseFilter);
                        }
                    });
                });
    }

    // Opsætning af sangtabellen
    private void songTableShow() {

        colSongtitle.setCellValueFactory(
                new PropertyValueFactory<>("title"));

        colSongArtist.setCellValueFactory(
                new PropertyValueFactory<>("artist"));

        colSongGenre.setCellValueFactory(
                new PropertyValueFactory<>("genre"));

        colSongTime.setCellValueFactory(
                new PropertyValueFactory<>("time"));

        SortedList<Song> sortedData =
                new SortedList<>(songModel.getObservableSongs());

        sortedData.comparatorProperty()
                .bind(tblSong.comparatorProperty());

        tblSong.setItems(sortedData);
    }

    // Opsætning af playlisttabellen
    private void playlistTableShow() {

        colPlaylistName.setCellValueFactory(
                new PropertyValueFactory<>("name"));

        colPlaylistSongs.setCellValueFactory(
                new PropertyValueFactory<>("numberOfSongs"));

        colPlaylistTime.setCellValueFactory(
                new PropertyValueFactory<>("totalTime"));

        SortedList<Playlist> sortedDataPlaylist =
                new SortedList<>(playlistModel.getObservablePlaylists());

        sortedDataPlaylist.comparatorProperty()
                .bind(tblPlaylist.comparatorProperty());

        tblPlaylist.setItems(sortedDataPlaylist);
    }

    // Opsætning af tabellen med sange i den valgte playlist
    private void playlistSongsTableShow() {

        colSongsInPlayllisttitle.setCellValueFactory(
                new PropertyValueFactory<>("title"));

        colSongsInPlayllistArtist.setCellValueFactory(
                new PropertyValueFactory<>("artist"));

        colSongsInPlayllistGenre.setCellValueFactory(
                new PropertyValueFactory<>("genre"));

        colSongsInPlayllistTime.setCellValueFactory(
                new PropertyValueFactory<>("time"));
    }

    // Henter og viser sangene fra den valgte playlist
    private void showSongsInPlaylist(Playlist playlist) {

        try {
            tblSongsInPlayllist.getItems().setAll(
                    playlistModel.getSongsFromPlaylist(playlist.getId())
            );

        } catch (Exception e) {
            displayError(e);
        }
    }

    // Viser fejl til brugeren
    private void displayError(Throwable t) {

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Something went wrong");
        alert.setHeaderText(t.getMessage());
        alert.showAndWait();
    }

    // Åbner vinduet til at tilføje en sang
    @FXML
    private void handleClickSongsCreate(ActionEvent actionEvent)
            throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(
                Application.class.getResource(
                        "/com/example/temufy/AddNew.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 520, 400);

        AddNewController controller = fxmlLoader.getController();
        controller.setModel(songModel);

        Stage stage = new Stage();
        stage.setTitle("Add Song");
        stage.setScene(scene);
        stage.show();
    }

    // Sletter en valgt sang fra sangbiblioteket
    @FXML
    private void handleClickSongsDelete(ActionEvent actionEvent)
            throws Exception {

        Song selectedSong =
                tblSong.getSelectionModel().getSelectedItem();

        if (selectedSong != null) {
            try {
                songModel.deleteSong(selectedSong);
            } catch (Exception err) {
                displayError(err);
            }
        }
    }

    // Aktiverer og deaktiverer søgefeltet
    @FXML
    private void handleClickSearch(ActionEvent actionEvent) {

        if ("Filter".equals(btnSearch.getText())) {

            txtSongSearch.setDisable(false);
            btnSearch.setText("Clear");

        } else if ("Clear".equals(btnSearch.getText())) {

            txtSongSearch.clear();
            txtSongSearch.setDisable(true);
            btnSearch.setText("Filter");
        }
    }

    @FXML
    private void handleClickNewPlaylist() {
        // Din eksisterende oprettelse af playlist skal indsættes her.
    }

    @FXML
    private void handleClickEdit() {
        // Din eksisterende redigering af playlist skal indsættes her.
    }

    @FXML
    private void handleClickDeletePlaylist() {
        // Din eksisterende sletning af playlist skal indsættes her.
    }

    @FXML
    public void handleClickSongsEdit(ActionEvent actionEvent) {
    }

    @FXML
    public void handleClickSongsInPlaylistAdd(ActionEvent actionEvent) {
    }

    @FXML
    public void handleClickSongsInPlaylistDelete(ActionEvent actionEvent) {
    }

    // Tilføjer den valgte sang til den valgte playlist
    @FXML
    public void handleClickAddSongToPlaylist(ActionEvent actionEvent) {

        try {
            Song selectedSong =
                    tblSong.getSelectionModel().getSelectedItem();

            Playlist selectedPlaylist =
                    tblPlaylist.getSelectionModel().getSelectedItem();

            if (selectedSong == null || selectedPlaylist == null) {

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setContentText(
                        "Vælg først en sang og en playliste."
                );
                alert.showAndWait();
                return;
            }

            playlistModel.addSongToPlaylist(
                    selectedPlaylist.getId(),
                    selectedSong.getId()
            );

            showSongsInPlaylist(selectedPlaylist);

        } catch (Exception e) {
            displayError(e);
        }
    }

    // Flytter den valgte sang én plads op
    @FXML
    private void handleMoveSongUp(ActionEvent event) {

        Song selectedSong =
                tblSongsInPlayllist.getSelectionModel().getSelectedItem();

        Playlist selectedPlaylist =
                tblPlaylist.getSelectionModel().getSelectedItem();

        if (selectedSong == null || selectedPlaylist == null) {
            return;
        }

        try {
            playlistModel.moveSongUp(
                    selectedPlaylist.getId(),
                    selectedSong.getId()
            );

            refreshPlaylistSongs(selectedPlaylist, selectedSong);

        } catch (Exception e) {
            displayError(e);
        }
    }

    // Flytter den valgte sang én plads ned
    @FXML
    private void handleMoveSongDown(ActionEvent event) {

        Song selectedSong =
                tblSongsInPlayllist.getSelectionModel().getSelectedItem();

        Playlist selectedPlaylist =
                tblPlaylist.getSelectionModel().getSelectedItem();

        if (selectedSong == null || selectedPlaylist == null) {
            return;
        }

        try {
            playlistModel.moveSongDown(
                    selectedPlaylist.getId(),
                    selectedSong.getId()
            );

            refreshPlaylistSongs(selectedPlaylist, selectedSong);

        } catch (Exception e) {
            displayError(e);
        }
    }

    // Genindlæser playlistens sange og beholder markeringen
    private void refreshPlaylistSongs(
            Playlist playlist, Song selectedSong) throws Exception {

        tblSongsInPlayllist.getItems().setAll(
                playlistModel.getSongsFromPlaylist(playlist.getId())
        );

        for (Song song : tblSongsInPlayllist.getItems()) {
            if (song.getId() == selectedSong.getId()) {
                tblSongsInPlayllist.getSelectionModel().select(song);
                tblSongsInPlayllist.scrollTo(song);
                break;
            }
        }
    }
}
