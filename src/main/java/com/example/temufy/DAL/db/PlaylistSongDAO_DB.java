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

    @Override
    public void removeSongFromPlaylist(int playlistId, int songId) throws SQLException {

        String selectSql = "SELECT TOP 1 Id, Position FROM PlaylistSong " +
                "WHERE PlaylistId = ? AND SongId = ? ORDER BY Position";
        String deleteSql = "DELETE FROM PlaylistSong WHERE Id = ?";
        String shiftSql = "UPDATE PlaylistSong SET Position = Position - 1 " +
                "WHERE PlaylistId = ? AND Position > ?";

        try (Connection conn = dbConnector.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement select = conn.prepareStatement(selectSql);
                 PreparedStatement delete = conn.prepareStatement(deleteSql);
                 PreparedStatement shift = conn.prepareStatement(shiftSql)) {

                select.setInt(1, playlistId);
                select.setInt(2, songId);
                ResultSet rs = select.executeQuery();
                if (!rs.next()) {
                    conn.rollback();
                    return;   // song was not in the playlist
                }
                int rowId = rs.getInt("Id");
                int removedPosition = rs.getInt("Position");

                delete.setInt(1, rowId);
                delete.executeUpdate();

                shift.setInt(1, playlistId);
                shift.setInt(2, removedPosition);
                shift.executeUpdate();

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    @Override
    public void moveSongUp(int playlistId, int position) throws SQLException {

        if (position <= 1) {
            return;   // already first, nothing to move
        }

        String sql = "UPDATE PlaylistSong " +
                "SET Position = CASE Position WHEN ? THEN ? WHEN ? THEN ? END " +
                "WHERE PlaylistId = ? AND Position IN (?, ?)";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, position);
            stmt.setInt(2, position - 1);
            stmt.setInt(3, position - 1);
            stmt.setInt(4, position);
            stmt.setInt(5, playlistId);
            stmt.setInt(6, position);
            stmt.setInt(7, position - 1);
            stmt.executeUpdate();
        }
    }
}
