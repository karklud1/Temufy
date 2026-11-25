package com.example.temufy.DAL;

import com.example.temufy.BE.Playlist;
import java.util.List;

public interface IPlaylistDataAccess {

    List<Playlist> getAllPlaylists() throws Exception;

    Playlist createPlaylist(Playlist newPlaylist) throws Exception;

    void updatePlaylist(Playlist playlist) throws Exception;

    void deletePlaylist(Playlist playlist) throws Exception;

}