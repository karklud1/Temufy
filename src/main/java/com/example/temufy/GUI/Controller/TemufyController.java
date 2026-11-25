package com.example.temufy.GUI.Controller;

import com.example.temufy.GUI.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class TemufyController {
    public TextField txtSongSearch;
    public Button btnSongCreate;
    public TableView tblPlaylist;
    public TableColumn colPlaylistName;
    public TableColumn colPlaylistSongs;
    public TableColumn colPlaylistTime;
    public TableView tblSong;
    public TableColumn colSongtitle;
    public TableColumn colSongArtist;
    public TableColumn colSongGenre;
    public TableColumn colSongTime;
    public Button btnSongDelete;
    public Button btnPlaylistNew;
    public Button btnPlaylistEdit;
    public Button btnPlaylistDelete;

    public void handleClickCreate(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("/com/example/temufy/AddNew.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 520, 400);

        Stage stage = new Stage();
        stage.setTitle("Add Song");
        stage.setScene(scene);
        stage.show();

    }

}
