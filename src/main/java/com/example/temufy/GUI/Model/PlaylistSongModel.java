package com.example.temufy.GUI.Model;

import com.example.temufy.BE.Playlist;
import com.example.temufy.BE.Song;
import com.example.temufy.BLL.PlaylistSongManager;
import com.example.temufy.DAL.db.PlaylistSongDAO_DB;

public class PlaylistSongModel {

    private final PlaylistSongManager playlistSongManager;

    public PlaylistSongModel() throws Exception {
        playlistSongManager = new PlaylistSongManager(new PlaylistSongDAO_DB());
    }

    public void addSongToPlaylist(Playlist playlist, Song song) throws Exception {
        playlistSongManager.addSongToPlaylist(playlist.getId(), song.getId());
    }

    public void removeSongFromPlaylist(Playlist playlist, Song song) throws Exception {
        playlistSongManager.removeSongFromPlaylist(playlist.getId(), song.getId());
    }
}