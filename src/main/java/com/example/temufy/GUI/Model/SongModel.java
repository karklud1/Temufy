package com.example.temufy.GUI.Model;

import com.example.temufy.BE.Song;
import com.example.temufy.BLL.SongManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import java.util.List;

public class SongModel {

    private FilteredList<Song> filteredList;
    private ObservableList<Song> songsToBeViewed;
    private SongManager songManager;

    public SongModel() throws Exception {
        songManager = new SongManager();
        songsToBeViewed = FXCollections.observableArrayList();
        songsToBeViewed.addAll(songManager.getAllSongs());
        filteredList = new FilteredList<>(songsToBeViewed);
    }

    public FilteredList<Song> getObservableSongs() {
        return filteredList;
    }

    public void searchSong(String query) throws Exception {
        List<Song> searchResults = songManager.searchSongs(query);
        songsToBeViewed.clear();
        songsToBeViewed.addAll(searchResults);
    }

    public void createSong(Song newSong) throws Exception {
        Song createdSong = songManager.createSong(newSong);
        songsToBeViewed.add(createdSong);
    }

    public void updateSong(Song updatedSong) throws Exception {
        songManager.updateSong(updatedSong);
        songsToBeViewed.set(songsToBeViewed.indexOf(updatedSong), updatedSong);
    }

    public void deleteSong(Song songToBeDeleted) throws Exception {
        songManager.deleteSong(songToBeDeleted);
        songsToBeViewed.remove(songToBeDeleted);
    }
}