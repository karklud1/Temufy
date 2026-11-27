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
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TemufyController implements Initializable {

    @FXML
    public TableColumn colSongsInPlaylistTitle, colSongsInPlaylistArtist, colSongsInPlaylistGenre, colSongsInPlaylistTime;
    @FXML
    private Label lblTotalSongs, lblSongTitle, lblSongArtist, lblTimeCurrent, lblTimeTotal;
    @FXML
    private TextField txtSongSearch;
    @FXML
    private Slider sldSongProgress, sldVolume;
    @FXML
    private Button btnPrevSong, btnPlayAndPause, btnNextSong, btnSearch;
    @FXML
    private TableView<Song> tblSong, tblSongsInPlaylist;
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

        lockColumns(tblSong);
        lockColumns(tblPlaylist);
        lockColumns(tblSongsInPlaylist);
    }

    // Keeps each column at its start width, and eats a mouse drag that starts on a header
    private void lockColumns(TableView<?> table) {
        for (TableColumn<?, ?> column : table.getColumns()) {
            column.setReorderable(false);
            column.setResizable(false);
            double width = column.getPrefWidth();
            column.setMinWidth(width);
            column.setMaxWidth(width);
        }

        // A user drag asks to resize one column. Layout passes null and may still fit the table.
        table.setColumnResizePolicy(features -> {
            if (features.getColumn() == null) {
                return TableView.CONSTRAINED_RESIZE_POLICY.call(features);
            }
            return Boolean.FALSE;
        });

        table.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
            if (isColumnHeader(event.getPickResult().getIntersectedNode(), table)) {
                event.consume();
            }
        });
    }

    // True when the mouse is on a column title or the thin resize strip beside it
    private boolean isColumnHeader(Node node, TableView<?> table) {
        while (node != null && node != table) {
            if (node.getStyleClass().contains("column-header")
                    || node.getStyleClass().contains("column-header-background")
                    || node.getStyleClass().contains("filler")) {
                return true;
            }
            node = node.getParent();
        }
        return false;
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

        colSongsInPlaylistTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colSongsInPlaylistArtist.setCellValueFactory(new PropertyValueFactory<>("artist"));
        colSongsInPlaylistGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        colSongsInPlaylistTime.setCellValueFactory(new PropertyValueFactory<>("timeFormatted"));
        tblSongsInPlaylist.setItems(playlistModel.getSongsInPlaylist());

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

        tblSongsInPlaylist.setRowFactory(table -> {
            TableRow<Song> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && row.getItem() instanceof Song song) {
                    playSelected(song, tblSongsInPlaylist.getItems(), true);
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

         if (selectedSong == null) {
             displayError(new Exception("Please select a song to delete."));
             return;
         }

         if (!confirmDelete("Delete the song \"" + selectedSong.getTitle() + "\"?")) {
             return;
         }

         try {
             songModel.deleteSong(selectedSong);
         } catch (Exception err) {
             displayError(err);
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
            playlistModel.loadSongsForPlaylist(selectedPlaylist);// refreshes the songs-in-playlist table
            tblPlaylist.refresh();;

        } catch (Exception e) {
            displayError(e);
        }
    }

    @FXML
    private void handleClickSongsInPlaylistDelete(ActionEvent actionEvent) {
        Song selectedSong = (Song) tblSongsInPlaylist.getSelectionModel().getSelectedItem();
        Playlist selectedPlaylist = tblPlaylist.getSelectionModel().getSelectedItem();

        if (selectedSong == null || selectedPlaylist == null) {
            displayError(new Exception("Please select a playlist and a song in it."));
            return;
        }

        try {
            playlistSongModel.removeSongFromPlaylist(selectedPlaylist, selectedSong);
            playlistModel.loadSongsForPlaylist(selectedPlaylist);   // refreshes the songs-in-playlist table
            tblPlaylist.refresh();
        } catch (Exception e) {
            displayError(e);
        }
    }

    @FXML
    private void handleClickSongsInPlaylistUp(ActionEvent actionEvent) {
        Playlist selectedPlaylist = tblPlaylist.getSelectionModel().getSelectedItem();
        int index = tblSongsInPlaylist.getSelectionModel().getSelectedIndex();

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
            tblSongsInPlaylist.getSelectionModel().select(index - 1);   // keep the moved song selected
        } catch (Exception e) {
            displayError(e);
        }
    }

    @FXML
    private void handleClickSongsInPlaylistDown(ActionEvent actionEvent) {
        Playlist selectedPlaylist = tblPlaylist.getSelectionModel().getSelectedItem();
        int index = tblSongsInPlaylist.getSelectionModel().getSelectedIndex();

        if (selectedPlaylist == null || index < 0) {
            displayError(new Exception("Please select a playlist and a song in it."));
            return;
        }

        if (index == tblSongsInPlaylist.getItems().size() - 1) {
            return;   // already last
        }

        try {
            playlistSongModel.moveSongDown(selectedPlaylist, index + 1);   // position = index + 1
            playlistModel.loadSongsForPlaylist(selectedPlaylist);          // refresh the table
            tblSongsInPlaylist.getSelectionModel().select(index + 1);     // keep the moved song selected
        } catch (Exception e) {
            displayError(e);
        }
    }

    @FXML
    private void handleClickPlaylistDelete() {
        Playlist selectedPlaylist = tblPlaylist.getSelectionModel().getSelectedItem();

        if (selectedPlaylist == null) {
            displayError(new Exception("Please select a playlist to delete."));
            return;
        }

        if (!confirmDelete("Delete the playlist \"" + selectedPlaylist.getName() + "\"?")) {
            return;
        }

        try {
            playlistModel.deletePlaylist(selectedPlaylist);
        } catch (Exception err) {
            displayError(err);
        }
    }

    @FXML
    public void handleClickSongsEdit(ActionEvent actionEvent) throws IOException {
        Song selectedSong = tblSong.getSelectionModel().getSelectedItem();

        if (selectedSong == null) {
            displayError(new Exception("Please select a song to edit."));
            return;
        }

        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("/com/example/temufy/AddNew.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 520, 400);

        AddNewController controller = fxmlLoader.getController();
        controller.setModel(songModel);
        controller.setSongToEdit(selectedSong);   // new

        Stage stage = new Stage();
        stage.setTitle("Edit Song");
        stage.setScene(scene);
        stage.show();
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

    // Asks the user to confirm. Returns true only if they press OK.
    private boolean confirmDelete(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm delete");
        alert.setHeaderText(message);
        alert.setContentText("This cannot be undone.");

        return alert.showAndWait().filter(button -> button == ButtonType.OK).isPresent();
    }
}