package com.example.temufy.BLL;

import com.example.temufy.BE.Song;
import com.example.temufy.BLL.util.SongSearcher;
import com.example.temufy.DAL.ISongDataAccess;
import com.example.temufy.DAL.db.SongDAO_DB;

import java.io.IOException;
import java.util.List;

public class SongManager {

    private SongSearcher songSearcher = new SongSearcher();
    private ISongDataAccess songDAO;

    public SongManager() throws IOException {

        //songDAO = new SongDAO_File();
        songDAO = new SongDAO_DB();
        //songDAO = new SongDAO_Mock();
    }

    public List<Song> getAllSongs() throws Exception {
        return songDAO.getAllSongs();
    }

    public List<Song> searchSongs(String query) throws Exception {
        List<Song> allSongs = getAllSongs();
        List<Song> searchResult = songSearcher.search(allSongs, query);
        return searchResult;
    }

    public Song createSong(Song newSong) throws Exception {
        return songDAO.createSong(newSong);
    }

    public void updateSong(Song updatedSong) throws Exception {
        songDAO.updateSong(updatedSong);
    }

    public void deleteSong(Song songToBeDeleted) throws Exception {
        songDAO.deleteSong(songToBeDeleted);
    }
}
