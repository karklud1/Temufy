
package com.example.temufy.DAL;

import com.example.temufy.BE.Playlist;
import com.example.temufy.BE.Song;

import java.util.List;

public interface IPlaylistDataAccess {

    List<Playlist> getAllPlaylists() throws Exception;

    Playlist createPlaylist(String name) throws Exception;

    void updatePlaylist(Playlist playlist) throws Exception;

    void deletePlaylist(Playlist playlist) throws Exception;

    List<Song> getSongsOnPlaylist(int playlistId) throws Exception;

    void addSongToPlaylist(int playlistId, int songId) throws Exception;

}
