package com.example.temufy.DAL;


import java.sql.SQLException;

public interface IPlaylistSongDataAccess {

    void addSongToPlaylist(int playlistId, int songId) throws SQLException;
}
