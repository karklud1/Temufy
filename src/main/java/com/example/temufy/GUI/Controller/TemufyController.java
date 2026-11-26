package com.example.temufy.GUI.Controller;

import com.example.temufy.BE.Playlist;
import com.example.temufy.BE.Song;
import com.example.temufy.GUI.Application;
import com.example.temufy.GUI.Model.*;

import javafx.collections.ListChangeListener;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

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
    private Label lblTotalSongs;
    @FXML
    private Label lblSongTitle;
    @FXML
    private Label lblSongArtist;
    @FXML
    private Label lblTimeCurrent;
    @FXML
    private Label lblTimeTotal;
    @FXML
    private TextField txtSongSearch;
    @FXML
    private Slider sldSongProgress;
    @FXML
    private Slider sldVolume;
    @FXML
    private Button btnPrevSong, btnPlayAndPause, btnNextSong;
    @FXML
    private Button btnSongCreate,btnSongDelete,btnPlaylistNew,btnPlaylistEdit,btnPlaylistDelete,btnSearch,btnClear;
    @FXML
    private TableView<Song> tblSong;
    @FXML
    private TableView<Playlist> tblPlaylist;
    @FXML
    private TableColumn<Song, String> colSongtitle,colSongArtist,colSongGenre,colSongTime,colPlaylistName,colPlaylistSongs,colPlaylistTime;

    private PlaylistSongModel playlistSongModel;
    private PlaylistModel playlistModel;
    private SongModel songModel;

    private TotalSongs totalSongs = new TotalSongs();

    private final PlayerModel player = new PlayerModel();

    private boolean draggingProgress;

    public TemufyController() {
        try {
            songModel = new SongModel();
            playlistModel = new PlaylistModel();
            playlistSongModel = new PlaylistSongModel();
        } catch (Exception e) {
            displayError(e);
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (songModel != null) {
            songTableShow();
            playlistTableShow();
            totalSongs.totalNumberSongs(lblTotalSongs, songModel);

            songModel.getObservableSongs().addListener((ListChangeListener<Song>) change -> {
                totalSongs.totalNumberSongs(lblTotalSongs, songModel);
            });

            searchSong();
            setupPlayer();
        }
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
        colSongTime.setCellValueFactory(new PropertyValueFactory<>("timeFormatted"));

        // Makes the table able to sort
        SortedList<Song> sortedData = new SortedList<>(songModel.getObservableSongs());
        sortedData.comparatorProperty().bind(tblSong.comparatorProperty());
        tblSong.setItems(sortedData);
    }


    private void playlistTableShow(){
        if (playlistModel == null) {
            return;
        }
        // playlist table
        colPlaylistName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPlaylistSongs.setCellValueFactory(new PropertyValueFactory<>("numberOfSongs"));
        colPlaylistTime.setCellValueFactory(new PropertyValueFactory<>("totalTimeFormatted"));

        // Makes the table able to sort
        SortedList<Playlist> sortedDataPlaylist = new SortedList<>(playlistModel.getObservablePlaylists());
        sortedDataPlaylist.comparatorProperty().bind(tblPlaylist.comparatorProperty());
        tblPlaylist.setItems(sortedDataPlaylist);

        colSongsInPlayllisttitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colSongsInPlayllistArtist.setCellValueFactory(new PropertyValueFactory<>("artist"));
        colSongsInPlayllistGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        colSongsInPlayllistTime.setCellValueFactory(new PropertyValueFactory<>("timeFormatted"));
        tblSongsInPlayllist.setItems(playlistModel.getSongsInPlaylist());

        tblPlaylist.getSelectionModel().selectedItemProperty().addListener((obs, oldPlaylist, selectedPlaylist) -> {
            try {
                playlistModel.loadSongsForPlaylist(selectedPlaylist);
            } catch (Exception e) {
                displayError(e);
            }
        });
    }

    // Wires the buttons, the two song tables and the sliders. Playback uses the mp3 file.
    private void setupPlayer() {
        player.setOnSongChanged(this::updatePlayerView);
        player.setOnError(this::displayError);
        player.setOnTimeChanged(this::updatePlayerTime);

        btnPrevSong.setOnAction(event -> player.previousSong());
        btnPlayAndPause.setOnAction(event -> handlePlayAndPause());
        btnNextSong.setOnAction(event -> player.nextSong());

        tblSong.setRowFactory(table -> {
            TableRow<Song> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    playSelected(row.getItem(), tblSong.getItems(), false);
                }
            });
            return row;
        });

        tblSongsInPlayllist.setRowFactory(table -> {
            TableRow<Song> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && row.getItem() instanceof Song song) {
                    playSelected(song, tblSongsInPlayllist.getItems(), true);
                }
            });
            return row;
        });

        sldVolume.setMin(0);
        sldVolume.setMax(100);
        sldVolume.setValue(80);
        sldVolume.valueProperty().addListener((obs, oldValue, newValue) ->
                player.setVolume(newValue.doubleValue() / 100.0));

        sldSongProgress.setMin(0);
        sldSongProgress.setOnMousePressed(event -> draggingProgress = true);
        sldSongProgress.setOnMouseReleased(event -> {
            draggingProgress = false;
            player.seek(sldSongProgress.getValue());
        });
    }

    private void handlePlayAndPause() {
        if (player.getCurrentSong() == null) {
            Song selected = tblSong.getSelectionModel().getSelectedItem();
            if (selected == null && !tblSong.getItems().isEmpty()) {
                selected = tblSong.getItems().get(0);
            }
            if (selected == null) {
                displayError(new Exception("Choose a song first."));
                return;
            }
            playSelected(selected, tblSong.getItems(), false);
            return;
        }
        player.togglePlayPause();
    }

    private void playSelected(Song song, List<?> songs, boolean loop) {
        List<Song> queue = new ArrayList<>();
        for (Object item : songs) {
            if (item instanceof Song oneSong) {
                queue.add(oneSong);
            }
        }
        try {
            player.playSong(song, queue, loop);
        } catch (Exception e) {
            displayError(e);
        }
    }

    private void updatePlayerView() {
        Song song = player.getCurrentSong();
        if (song == null) {
            lblSongTitle.setText("Song Name");
            lblSongArtist.setText("Artist Name");
            btnPlayAndPause.setText("▶");
            return;
        }
        lblSongTitle.setText(song.getTitle());
        lblSongArtist.setText(song.getArtist());
        btnPlayAndPause.setText(player.isPlaying() ? "⏸" : "▶");
    }

    private void updatePlayerTime(double current, double total) {
        lblTimeCurrent.setText(formatTime(current));
        lblTimeTotal.setText(formatTime(total));
        if (!draggingProgress) {
            sldSongProgress.setMax(total > 0 ? total : 0);
            sldSongProgress.setValue(current);
        }
    }

    // 277 seconds becomes 04:37
    private String formatTime(double seconds) {
        int totalSeconds = (int) Math.round(seconds);
        if (totalSeconds < 0) {
            totalSeconds = 0;
        }
        int minutes = totalSeconds / 60;
        int secs = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, secs);
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
    // Opens the small window where the user types a playlist name
    @FXML
    private void handleClickPlaylistNew() throws IOException {
        if (playlistModel == null) {
            displayError(new Exception("Playlists could not be loaded."));
            return;
        }

        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("/com/example/temufy/NewPlaylist.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 420, 220);

        NewPlaylistController controller = fxmlLoader.getController();
        controller.setModel(playlistModel);

        Stage stage = new Stage();
        stage.setTitle("New playlist");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void handleClickSongsInPlaylistAdd(ActionEvent actionEvent) {
        Song selectedSong = tblSong.getSelectionModel().getSelectedItem();
        Playlist selectedPlaylist = tblPlaylist.getSelectionModel().getSelectedItem();

        if (selectedSong == null || selectedPlaylist == null) {
            displayError(new Exception("Please select a song and a playlist."));
            return;
        }

        try {
            playlistSongModel.addSongToPlaylist(selectedPlaylist, selectedSong);
            playlistModel.loadSongsForPlaylist(selectedPlaylist);   // refreshes the songs-in-playlist table
        } catch (Exception e) {
            displayError(e);
        }
    }

    @FXML
    private void handleClickSongsInPlaylistDelete(ActionEvent actionEvent) {
        Song selectedSong = (Song) tblSongsInPlayllist.getSelectionModel().getSelectedItem();
        Playlist selectedPlaylist = tblPlaylist.getSelectionModel().getSelectedItem();

        if (selectedSong == null || selectedPlaylist == null) {
            displayError(new Exception("Please select a playlist and a song in it."));
            return;
        }

        try {
            playlistSongModel.removeSongFromPlaylist(selectedPlaylist, selectedSong);
            playlistModel.loadSongsForPlaylist(selectedPlaylist);   // refreshes the songs-in-playlist table
        } catch (Exception e) {
            displayError(e);
        }
    }

    @FXML
    private void handleClickSongsInPlaylistUp(ActionEvent actionEvent) {
        Playlist selectedPlaylist = tblPlaylist.getSelectionModel().getSelectedItem();
        int index = tblSongsInPlayllist.getSelectionModel().getSelectedIndex();

        if (selectedPlaylist == null || index < 0) {
            displayError(new Exception("Please select a playlist and a song in it."));
            return;
        }

        if (index == 0) {
            return;   // already first
        }

        try {
            playlistSongModel.moveSongUp(selectedPlaylist, index + 1);   // position = index + 1
            playlistModel.loadSongsForPlaylist(selectedPlaylist);        // refresh the table
            tblSongsInPlayllist.getSelectionModel().select(index - 1);   // keep the moved song selected
        } catch (Exception e) {
            displayError(e);
        }
    }

    @FXML
    private void handleClickSongsInPlaylistDown(ActionEvent actionEvent) {
        Playlist selectedPlaylist = tblPlaylist.getSelectionModel().getSelectedItem();
        int index = tblSongsInPlayllist.getSelectionModel().getSelectedIndex();

        if (selectedPlaylist == null || index < 0) {
            displayError(new Exception("Please select a playlist and a song in it."));
            return;
        }

        if (index == tblSongsInPlayllist.getItems().size() - 1) {
            return;   // already last
        }

        try {
            playlistSongModel.moveSongDown(selectedPlaylist, index + 1);   // position = index + 1
            playlistModel.loadSongsForPlaylist(selectedPlaylist);          // refresh the table
            tblSongsInPlayllist.getSelectionModel().select(index + 1);     // keep the moved song selected
        } catch (Exception e) {
            displayError(e);
        }
    }

    @FXML
    private void handleClickPlaylistDelete(){
        Playlist selectedPlaylist = tblPlaylist.getSelectionModel().getSelectedItem();

        if (selectedPlaylist != null) {
            try {
                playlistModel.deletePlaylist(selectedPlaylist);
            } catch (Exception err) {
                displayError(err);
            }
        }
    }

    public void handleClickSongsEdit(ActionEvent actionEvent) {
    }

    @FXML
    private void handleClickPlaylistEdit(ActionEvent actionEvent) throws IOException {
        Playlist selectedPlaylist = tblPlaylist.getSelectionModel().getSelectedItem();

        if (selectedPlaylist == null) {
            displayError(new Exception("Please select a playlist to edit."));
            return;
        }

        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("/com/example/temufy/NewPlaylist.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 420, 220);

        NewPlaylistController controller = fxmlLoader.getController();
        controller.setModel(playlistModel);
        controller.setPlaylistToEdit(selectedPlaylist);

        Stage stage = new Stage();
        stage.setTitle("Edit playlist");
        stage.setScene(scene);
        stage.show();
    }
}