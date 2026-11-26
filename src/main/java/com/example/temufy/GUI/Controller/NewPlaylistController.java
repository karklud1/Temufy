package com.example.temufy.GUI.Controller;

import com.example.temufy.BE.Playlist;
import com.example.temufy.GUI.Model.PlaylistModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

// The small window for a new playlist name. Saving goes through PlaylistModel, not the database.
public class NewPlaylistController {

    private PlaylistModel playlistModel;
    private Playlist playlistToEdit;

    @FXML
    private TextField txtName;

    // The main window passes its playlist model, so the new row shows up in the same table
    public void setModel(PlaylistModel playlistModel) {
        this.playlistModel = playlistModel;
    }

    // Called by the main window when the user presses Edit
    public void setPlaylistToEdit(Playlist playlist) {
        this.playlistToEdit = playlist;
        txtName.setText(playlist.getName());   // show the current name
    }

    // Saves a playlist with no songs yet. The database makes the id.
    @FXML
    public void handleClickSave(ActionEvent actionEvent) {
        try {
            String name = txtName.getText().trim();
            if (name.isEmpty()) {
                throw new Exception("Please enter a name.");
            }

            if (playlistToEdit == null) {
                playlistModel.createPlaylist(new Playlist(0, name, 0, 0));
            } else {
                playlistToEdit.setName(name);
                playlistModel.updatePlaylist(playlistToEdit);
            }

            Stage stage = (Stage) txtName.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            displayError(e);
        }
    }

    // Closes the window without saving
    @FXML
    public void handleClickCancel(ActionEvent actionEvent) {
        Stage stage = (Stage) txtName.getScene().getWindow();
        stage.close();
    }

    // Same alert style as the main window
    private void displayError(Throwable t) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Something went wrong");
        alert.setHeaderText(t.getMessage());
        alert.showAndWait();
    }
}
