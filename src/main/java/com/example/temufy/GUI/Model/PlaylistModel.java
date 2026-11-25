
package com.example.temufy.GUI.Model;

import com.example.temufy.BE.Playlist;
import com.example.temufy.BE.Song;
import com.example.temufy.BLL.PlaylistManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import java.util.List;

public class PlaylistModel {

    private FilteredList<Playlist> filteredList;
    private ObservableList<Playlist> playlistsToBeViewed;
    private PlaylistManager playlistManager;

    public PlaylistModel() throws Exception {
        playlistManager = new PlaylistManager();

        playlistsToBeViewed = FXCollections.observableArrayList();
        playlistsToBeViewed.addAll(playlistManager.getAllPlaylists());

        filteredList = new FilteredList<>(playlistsToBeViewed);
    }

    public FilteredList<Playlist> getObservablePlaylists() {
        return filteredList;
    }

    public void createPlaylist(Playlist newPlaylist) throws Exception {
        Playlist createdPlaylist = playlistManager.createPlaylist(newPlaylist);
        playlistsToBeViewed.add(createdPlaylist);
    }

    public void updatePlaylist(Playlist updatedPlaylist) throws Exception {
        playlistManager.updatePlaylist(updatedPlaylist);
    }

    public void deletePlaylist(Playlist playlistToBeDeleted) throws Exception {
        playlistManager.deletePlaylist(playlistToBeDeleted);
        playlistsToBeViewed.remove(playlistToBeDeleted);
    }

    // Henter alle sange fra en bestemt playlist
    public List<Song> getSongsFromPlaylist(int playlistId) throws Exception {
        return playlistManager.getSongsFromPlaylist(playlistId);
    }

    // Flytter en sang én position op
    public void moveSongUp(int playlistId, int songId) throws Exception {
        playlistManager.moveSongUp(playlistId, songId);
    }

    // Flytter en sang én position ned
    public void moveSongDown(int playlistId, int songId) throws Exception {
        playlistManager.moveSongDown(playlistId, songId);
    }

    public void addSongToPlaylist(int playlistId, int songId) throws Exception {
        playlistManager.addSongToPlaylist(playlistId, songId);
    }
}
