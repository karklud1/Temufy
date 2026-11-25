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
    public TableView tblSongsInPlayllist;
    public TableColumn colSongsInPlayllisttitle;
    public TableColumn colSongsInPlayllistArtist;
    public TableColumn colSongsInPlayllistGenre;
    public TableColumn colSongsInPlayllistTime;
    public Button btncolSongsInPlayllistAdd;
    public Button btncolSongsInPlayllistDelete;
    public Button btnSongsNew;
    public Button btnSongsEdit;
    public Button btnSongsDelete;
    //FXML variables
    @FXML
    private TextField txtSongSearch;
    @FXML
    private Button btnSongCreate,btnSongDelete,btnPlaylistNew,btnPlaylistEdit,btnPlaylistDelete,btnSearch,btnClear;
    @FXML
    private TableView<Song> tblSong;
    @FXML
    private TableView<Playlist> tblPlaylist;
    @FXML
    private TableColumn<Song, String> colSongtitle,colSongArtist,colSongGenre,colSongTime,colPlaylistName,colPlaylistSongs,colPlaylistTime;

    private PlaylistModel playlistModel;

    private SongModel songModel;

    public TemufyController() {
        try {
            songModel = new SongModel();
            playlistModel = new PlaylistModel();
        } catch (Exception e) {
            displayError(e);
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        songTableShow();
        playlistTableShow();

        searchSong();
    }

    private void searchSong(){
        // Lyt til søgefeltet (filtrerer FilteredList'en i modellen)
        txtSongSearch.setDisable(true);
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
    }


    private void songTableShow(){

        // Opsæt kolonner i tableview
        colSongtitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colSongArtist.setCellValueFactory(new PropertyValueFactory<>("artist"));
        colSongGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        colSongTime.setCellValueFactory(new PropertyValueFactory<>("time"));

        // Makes the table able to sort
        SortedList<Song> sortedData = new SortedList<>(songModel.getObservableSongs());
        sortedData.comparatorProperty().bind(tblSong.comparatorProperty());
        tblSong.setItems(sortedData);
    }


    private void playlistTableShow(){
        // playlist table
        colPlaylistName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPlaylistSongs.setCellValueFactory(new PropertyValueFactory<>("numberOfSongs"));
        colPlaylistTime.setCellValueFactory(new PropertyValueFactory<>("totalTime"));

        // Makes the table able to sort
        SortedList<Playlist> sortedDataPlaylist = new SortedList<>(playlistModel.getObservablePlaylists());
        sortedDataPlaylist.comparatorProperty().bind(tblPlaylist.comparatorProperty());
        tblPlaylist.setItems(sortedDataPlaylist);
    }

    // Displays error for user when a checked error happens
    private void displayError(Throwable t) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Something went wrong");
        alert.setHeaderText(t.getMessage());
        alert.showAndWait();
    }

    // Opens add song window
    @FXML
    private void handleClickSongsCreate(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("/com/example/temufy/AddNew.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 520, 400);

        AddNewController controller = fxmlLoader.getController();
        controller.setModel(songModel);

        Stage stage = new Stage();
        stage.setTitle("Add Song");
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    private void handleClickCreate2(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("/com/example/temufy/AddNew2.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 520, 400);

        AddNewController2 controller = fxmlLoader.getController();
        controller.setModel(songModel);

        Stage stage = new Stage();
        stage.setTitle("Add Song");
        stage.setScene(scene);
        stage.show();
    }

     // Deletes selected song
    @FXML
    private void handleClickSongsDelete(ActionEvent actionEvent) throws Exception {
        Song selectedSong = tblSong.getSelectionModel().getSelectedItem();

        if (selectedSong != null) {
            try {
                songModel.deleteSong(selectedSong);
            } catch (Exception err) {
                displayError(err);
            }
        }
    }

    @FXML
    private void handleClickSearch(ActionEvent actionEvent) throws Exception{
        if ("Filter".equals(btnSearch.getText())) {
            txtSongSearch.setDisable(false);
            btnSearch.setText("Clear");
        } else if ("Clear".equals(btnSearch.getText())) {
            txtSongSearch.setDisable(true);
            btnSearch.setText("Filter");
        }
    }
    @FXML
    private void handleClickNewPlaylist(){

    }
    @FXML
    private void handleClickEdit(){

    }
    @FXML
    private void handleClickDeletePlaylist(){

    }

    public void handleClickSongsEdit(ActionEvent actionEvent) {
    }

    public void handleClickSongsInPlaylistAdd(ActionEvent actionEvent) {
    }

    public void handleClickSongsInPlaylistDelete(ActionEvent actionEvent) {
    }
}