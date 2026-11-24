package com.example.temufy.DAL;

import com.example.temufy.BE.Playlist;
import com.example.temufy.BE.Song;
import java.util.List;

public interface IPlaylistDataAccess {

    List<Song> getAllPlaylists() throws Exception;

    Playlist createPlaylist(Playlist newPlaylist) throws Exception;

    void updatePlaylist(Playlist playlist) throws Exception;

    void deletePlaylist(Playlist playlist) throws Exception;

}