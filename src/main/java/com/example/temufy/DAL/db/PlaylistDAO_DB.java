
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

        String sql = "SELECT * FROM Playlist";

        try (Connection conn = dbConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("Id");
                String name = rs.getString("Name");

                playlists.add(new Playlist(id, name, 0, 0));
            }
        }

        return playlists;
    }

    @Override
    public Playlist createPlaylist(Playlist playlist) throws Exception {

        String sql = "INSERT INTO Playlist (Name) VALUES (?)";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, playlist.getName());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    return new Playlist(id, playlist.getName(), 0, 0);
                }
            }
        }

        throw new SQLException("Could not create playlist.");
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

    // Henter alle sange i en playlist i den rigtige rækkefølge
    @Override
    public List<Song> getSongsFromPlaylist(int playlistId) throws Exception {

        List<Song> songs = new ArrayList<>();

        String sql = """
                SELECT s.Id, s.Title, s.Artist, s.Genre, s.Time, s.FilePath
                FROM Song s
                JOIN PlaylistSong ps ON s.Id = ps.SongId
                WHERE ps.PlaylistId = ?
                ORDER BY ps.Position
                """;

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, playlistId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    songs.add(new Song(
                            rs.getString("Title"),
                            rs.getString("Artist"),
                            rs.getString("Genre"),
                            rs.getDouble("Time"),
                            rs.getString("FilePath"),
                            rs.getInt("Id")
                    ));
                }
            }
        }

        return songs;
    }

    // Tilføjer sangen sidst i playlisten
    @Override
    public void addSongToPlaylist(int playlistId, int songId) throws Exception {

        String sql = """
                INSERT INTO PlaylistSong (PlaylistId, SongId, Position)
                SELECT ?, ?, COALESCE(MAX(Position), 0) + 1
                FROM PlaylistSong
                WHERE PlaylistId = ?
                """;

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, playlistId);
            stmt.setInt(2, songId);
            stmt.setInt(3, playlistId);

            stmt.executeUpdate();
        }
    }

    // Fjerner kun forbindelsen til playlisten - ikke selve sangen
    @Override
    public void removeSongFromPlaylist(int playlistId, int songId) throws Exception {

        String sql = """
                DELETE FROM PlaylistSong
                WHERE PlaylistId = ? AND SongId = ?
                """;

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, playlistId);
            stmt.setInt(2, songId);

            stmt.executeUpdate();
        }
    }

    // Flytter sangen én plads op
    @Override
    public void moveSongUp(int playlistId, int songId) throws Exception {
        moveSong(playlistId, songId, true);
    }

    // Flytter sangen én plads ned
    @Override
    public void moveSongDown(int playlistId, int songId) throws Exception {
        moveSong(playlistId, songId, false);
    }

    private void moveSong(int playlistId, int songId, boolean up)
            throws Exception {

        String selectedSql = """
                SELECT Position
                FROM PlaylistSong
                WHERE PlaylistId = ? AND SongId = ?
                """;

        try (Connection conn = dbConnector.getConnection()) {

            conn.setAutoCommit(false);

            try {
                int currentPosition;

                try (PreparedStatement stmt =
                             conn.prepareStatement(selectedSql)) {

                    stmt.setInt(1, playlistId);
                    stmt.setInt(2, songId);

                    try (ResultSet rs = stmt.executeQuery()) {
                        if (!rs.next()) {
                            conn.commit();
                            return;
                        }

                        currentPosition = rs.getInt("Position");
                    }
                }

                String neighbourSql = up
                        ? """
                        SELECT SongId, Position
                        FROM PlaylistSong
                        WHERE PlaylistId = ? AND Position < ?
                        ORDER BY Position DESC
                        LIMIT 1
                        """
                        : """
                        SELECT SongId, Position
                        FROM PlaylistSong
                        WHERE PlaylistId = ? AND Position > ?
                        ORDER BY Position ASC
                        LIMIT 1
                        """;

                int neighbourSongId;
                int neighbourPosition;

                try (PreparedStatement stmt =
                             conn.prepareStatement(neighbourSql)) {

                    stmt.setInt(1, playlistId);
                    stmt.setInt(2, currentPosition);

                    try (ResultSet rs = stmt.executeQuery()) {
                        if (!rs.next()) {
                            conn.commit();
                            return;
                        }

                        neighbourSongId = rs.getInt("SongId");
                        neighbourPosition = rs.getInt("Position");
                    }
                }

                // Brug en midlertidig position, så positionerne byttes sikkert
                String updateSql = """
                        UPDATE PlaylistSong
                        SET Position = ?
                        WHERE PlaylistId = ? AND SongId = ?
                        """;

                try (PreparedStatement stmt =
                             conn.prepareStatement(updateSql)) {

                    stmt.setInt(1, -1);
                    stmt.setInt(2, playlistId);
                    stmt.setInt(3, songId);
                    stmt.executeUpdate();

                    stmt.setInt(1, currentPosition);
                    stmt.setInt(2, playlistId);
                    stmt.setInt(3, neighbourSongId);
                    stmt.executeUpdate();

                    stmt.setInt(1, neighbourPosition);
                    stmt.setInt(2, playlistId);
                    stmt.setInt(3, songId);
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
}
