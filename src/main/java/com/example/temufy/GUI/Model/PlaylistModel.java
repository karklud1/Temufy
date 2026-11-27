package com.example.temufy.GUI.Model;

import com.example.temufy.BE.Playlist;
import com.example.temufy.BE.Song;
import com.example.temufy.BLL.PlaylistManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;


public class PlaylistModel {

    private FilteredList<Playlist> filteredList;
    private ObservableList<Playlist> playlistsToBeViewed;
    private ObservableList<Song> songsInPlaylist;
    private PlaylistManager playlistManager;

    public PlaylistModel() throws Exception {
        playlistManager = new PlaylistManager();
        playlistsToBeViewed = FXCollections.observableArrayList();
        playlistsToBeViewed.addAll(playlistManager.getAllPlaylists());
        filteredList = new FilteredList<>(playlistsToBeViewed);
        songsInPlaylist = FXCollections.observableArrayList();
    }

    public FilteredList<Playlist> getObservablePlaylists() { return filteredList; }

    public ObservableList<Song> getSongsInPlaylist() { return songsInPlaylist; }

    // Fills the middle table when the user clicks a playlist
    public void loadSongsForPlaylist(Playlist playlist) throws Exception {
        songsInPlaylist.clear();
        if (playlist != null) {
            songsInPlaylist.addAll(playlistManager.getSongsForPlaylist(playlist.getId()));

            playlist.setNumberOfSongs(songsInPlaylist.size());
            playlist.setTotalTime(songsInPlaylist.stream().mapToDouble(Song::getTime).sum());
        }
    }

    public void createPlaylist(Playlist newPlaylist) throws Exception {
        Playlist createdPlaylist = playlistManager.createPlaylist(newPlaylist);
        playlistsToBeViewed.add(createdPlaylist);
    }

    public void updatePlaylist(Playlist updatedPlaylist) throws Exception {
        playlistManager.updatePlaylist(updatedPlaylist);
        playlistsToBeViewed.set(playlistsToBeViewed.indexOf(updatedPlaylist), updatedPlaylist);
    }

    public void deletePlaylist(Playlist playlistToBeDeleted) throws Exception {
        playlistManager.deletePlaylist(playlistToBeDeleted);
        playlistsToBeViewed.remove(playlistToBeDeleted);
    }
    public void refreshPlaylist() throws Exception{
        playlistsToBeViewed.setAll(playlistManager.getAllPlaylists());
    }
}
