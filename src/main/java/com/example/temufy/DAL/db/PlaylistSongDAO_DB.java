package com.example.temufy.DAL.db;

import com.example.temufy.DAL.IPlaylistSongDataAccess;

import java.io.IOException;
import java.sql.*;

public class PlaylistSongDAO_DB implements IPlaylistSongDataAccess {

    private final DBConnector dbConnector = new DBConnector();

    public PlaylistSongDAO_DB() throws IOException {
    }

    @Override
    public void addSongToPlaylist(int playlistId, int songId) throws SQLException {
        String sql = "INSERT INTO playlistSong (playlistid, songid, position) " +
                "SELECT ?, ?, COALESCE(MAX(position), 0) + 1 " +
                "FROM playlistSong WHERE playlistid = ?";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, playlistId);
            stmt.setInt(2, songId);
            stmt.setInt(3, playlistId);
            stmt.executeUpdate();
        }
    }
}
