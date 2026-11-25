package com.example.temufy.BLL;

import com.example.temufy.BE.Playlist;
import com.example.temufy.DAL.IPlaylistDataAccess;
import com.example.temufy.DAL.db.PlaylistDAO_DB;

import java.io.IOException;
import java.util.List;

public class PlaylistManager {

    private IPlaylistDataAccess playlistDAO;

    public PlaylistManager() throws IOException {
        playlistDAO = new PlaylistDAO_DB();
    }

    public List<Playlist> getAllPlaylists() throws Exception {
        return playlistDAO.getAllPlaylists();
    }

    public Playlist createPlaylist(Playlist playlist) throws Exception {
        return playlistDAO.createPlaylist(playlist);
    }

    public void updatePlaylist(Playlist updatedPlaylist) throws Exception {
        playlistDAO.updatePlaylist(updatedPlaylist);
    }

    public void deletePlaylist(Playlist playlistToBeDeleted) throws Exception {
        playlistDAO.deletePlaylist(playlistToBeDeleted);
    }
}