
package com.example.temufy.DAL.db;

import com.example.temufy.BE.Playlist;
import com.example.temufy.BE.Song;
import com.example.temufy.DAL.IPlaylistDataAccess;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlaylistDAO_DB implements IPlaylistDataAccess {

    private final DBConnector dbConnector = new DBConnector();

    public PlaylistDAO_DB() throws IOException {
    }

    @Override
    public List<Playlist> getAllPlaylists() throws Exception {
        List<Playlist> playlists = new ArrayList<>();

        String sql = "SELECT p.Id, p.Name, COUNT(ps.SongId) AS SongCount, " +
                "ISNULL(SUM(s.Playtime), 0) AS PlaylistTime " +
                "FROM Playlist p " +
                "LEFT JOIN PlaylistSong ps ON ps.PlaylistId = p.Id " +
                "LEFT JOIN Songs s ON s.Id = ps.SongId " +
                "GROUP BY p.Id, p.Name";

        try (Connection conn = dbConnector.getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                playlists.add(new Playlist(
                        rs.getInt("Id"),
                        rs.getString("Name"),
                        rs.getInt("SongCount"),
                        rs.getInt("PlaylistTime")));
            }
        }
        return playlists;
    }

    @Override
    public Playlist createPlaylist(String name) throws Exception {
        String sql = "INSERT INTO Playlist (Name) VALUES (?);";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, name);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            int id = 0;
            if (rs.next()) {
                id = rs.getInt(1);
            }
            return new Playlist(id, name, 0, 0);
        }
    }

    @Override
    public void updatePlaylist(Playlist playlist) throws Exception {

        String sql = "UPDATE Playlist SET Name = ? WHERE Id = ?";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, playlist.getName());
            stmt.setInt(2, playlist.getId());

            stmt.executeUpdate();
        }
    }

    @Override
    public void deletePlaylist(Playlist playlist) throws Exception {

        String deleteSongsSql =
                "DELETE FROM PlaylistSong WHERE PlaylistId = ?";

        String deletePlaylistSql =
                "DELETE FROM Playlist WHERE Id = ?";

        try (Connection conn = dbConnector.getConnection()) {

            conn.setAutoCommit(false);

            try {
                try (PreparedStatement stmt =
                             conn.prepareStatement(deleteSongsSql)) {
                    stmt.setInt(1, playlist.getId());
                    stmt.executeUpdate();
                }

                try (PreparedStatement stmt =
                             conn.prepareStatement(deletePlaylistSql)) {
                    stmt.setInt(1, playlist.getId());
                    stmt.executeUpdate();
                }

                conn.commit();

            } catch (Exception e) {
                conn.rollback();
                throw e;

            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    @Override
    public List<Song> getSongsOnPlaylist(int playlistId) throws Exception {
        List<Song> songs = new ArrayList<>();

        String sql = "SELECT s.* FROM Songs s " +
                "JOIN PlaylistSong ps ON ps.SongId = s.Id " +
                "WHERE ps.PlaylistId = ? " +
                "ORDER BY ps.Id";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, playlistId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                songs.add(new Song(
                        rs.getString("Title"),
                        rs.getString("Artist"),
                        rs.getString("Genre"),
                        rs.getDouble("time"),
                        rs.getString("FilePath"),
                        rs.getInt("id")));
            }
        }
        return songs;
    }

    @Override
    public void addSongToPlaylist(int playlistId, int songId) throws Exception {
        String sql = "INSERT INTO PlaylistSong (PlaylistId, SongId) VALUES (?, ?);";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, playlistId);
            stmt.setInt(2, songId);
            stmt.executeUpdate();
        }
    }
}
