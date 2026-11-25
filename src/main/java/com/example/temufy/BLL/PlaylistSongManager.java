package com.example.temufy.BLL;

import com.example.temufy.DAL.IPlaylistSongDataAccess;

public class PlaylistSongManager {

    private final IPlaylistSongDataAccess playlistDAO;

    public PlaylistSongManager(IPlaylistSongDataAccess playlistDAO) {
        this.playlistDAO = playlistDAO;
    }

    public void addSongToPlaylist(int playlistId, int songId) throws Exception {
        // room for rules later, e.g. check the song isn't already in the playlist
        playlistDAO.addSongToPlaylist(playlistId, songId);
    }
}
