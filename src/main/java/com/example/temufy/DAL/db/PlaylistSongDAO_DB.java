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
        String insertSql = "INSERT INTO playlistSong (playlistid, songid, position) " +
                "SELECT ?, ?, COALESCE(MAX(Position), 0) + 1 " +
                "FROM PlaylistSong WHERE playlistid = ?";

        String updatePlaylistSQL = "UPDATE Playlist " +
                "SET SongCount = SongCount + 1, " +
                "    PLAYTIME = PLAYTIME + (SELECT [Time] FROM Song WHERE Id = ?) " +
                "WHERE Id = ?";

        try (Connection conn = dbConnector.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                 PreparedStatement updateStmt = conn.prepareStatement(updatePlaylistSQL)) {

                insertStmt.setInt(1, playlistId);
                insertStmt.setInt(2, songId);
                insertStmt.setInt(3, playlistId);
                insertStmt.executeUpdate();

                updateStmt.setInt(1, songId);
                updateStmt.setInt(2, playlistId);
                updateStmt.executeUpdate();

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }
        @Override
        public void removeSongFromPlaylist ( int playlistId, int songId) throws SQLException {

            String selectSql = "SELECT TOP 1 ps.id, ps.Position, s.[Time] " +
                    "FROM PlaylistSong ps " +
                    "INNER JOIN Song s ON ps.SongId = s.Id " +
                    "WHERE ps.PlaylistId = ? AND ps.SongId = ? " +
                    "ORDER BY ps.Position";
            String deleteSql = "DELETE FROM PlaylistSong WHERE Id = ?";
            String shiftSql = "UPDATE PlaylistSong SET Position = Position - 1 " +
                    "WHERE PlaylistId = ? AND Position > ?";
            String updatePlaylistSQL = "UPDATE Playlist " + "SET SongCount = SongCount - 1, PLAYTIME = PLAYTIME - ? " +
                    "WHERE Id = ?";


            try (Connection conn = dbConnector.getConnection()) {
                conn.setAutoCommit(false);
                try (PreparedStatement select = conn.prepareStatement(selectSql);
                     PreparedStatement delete = conn.prepareStatement(deleteSql);
                     PreparedStatement shift = conn.prepareStatement(shiftSql);
                     PreparedStatement updatePlaylist = conn.prepareStatement(updatePlaylistSQL)) {

                    select.setInt(1, playlistId);
                    select.setInt(2, songId);
                    try (ResultSet rs = select.executeQuery()) {
                        if (!rs.next()) {
                            conn.rollback();
                            return;   // song was not in the playlist
                        }

                        int rowId = rs.getInt("Id");
                        int removedPosition = rs.getInt("Position");
                        int songTime = rs.getInt("Time");

                        delete.setInt(1, rowId);
                        delete.executeUpdate();

                        shift.setInt(1, playlistId);
                        shift.setInt(2, removedPosition);
                        shift.executeUpdate();

                        updatePlaylist.setDouble(1, songTime);
                        updatePlaylist.setInt(2, playlistId);
                        updatePlaylist.executeUpdate();
                    }

                    conn.commit();
                } catch (SQLException e) {
                    conn.rollback();
                    throw e;
                }
            }
        }

        @Override
        public void moveSongUp ( int playlistId, int position) throws SQLException {

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

        @Override
        public void moveSongDown ( int playlistId, int position) throws SQLException {

            String sql = "UPDATE PlaylistSong " +
                    "SET Position = CASE Position WHEN ? THEN ? WHEN ? THEN ? END " +
                    "WHERE PlaylistId = ? AND Position IN (?, ?) " +
                    "AND EXISTS (SELECT 1 FROM PlaylistSong WHERE PlaylistId = ? AND Position = ?)";

            try (Connection conn = dbConnector.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, position);
                stmt.setInt(2, position + 1);
                stmt.setInt(3, position + 1);
                stmt.setInt(4, position);
                stmt.setInt(5, playlistId);
                stmt.setInt(6, position);
                stmt.setInt(7, position + 1);
                stmt.setInt(8, playlistId);
                stmt.setInt(9, position + 1);
                stmt.executeUpdate();
            }
        }
    }
