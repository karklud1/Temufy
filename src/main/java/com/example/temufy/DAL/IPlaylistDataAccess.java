package com.example.temufy.DAL;

import com.example.temufy.BE.Playlist;
import com.example.temufy.BE.Song;

import java.util.List;

public interface IPlaylistDataAccess {

    List<Playlist> getAllPlaylists() throws Exception;

    // Songs linked to one playlist. The audio file stays in the mp3 folder.
    List<Song> getSongsForPlaylist(int playlistId) throws Exception;

    Playlist createPlaylist(Playlist newPlaylist) throws Exception;

    void updatePlaylist(Playlist playlist) throws Exception;

    void deletePlaylist(Playlist playlist) throws Exception;

}