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

        try (Connection conn = dbConnector.getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery("SELECT * FROM Playlist");

            while (rs.next()) {

                int id = rs.getInt("Id");
                String name = rs.getString("Name");
                int songs = rs.getInt("SongCount");
                int time = rs.getInt("PLAYTIME");

                playlists.add(
                        new Playlist(
                                id,
                                name,
                                songs,
                                time
                        )
                );
            }
        }

        return playlists;
    }

    // PlaylistSong only stores ids. The file path still points at the mp3 folder.
    @Override
    public List<Song> getSongsForPlaylist(int playlistId) throws Exception {
        String sql = "SELECT s.Id, s.Title, s.Artist, s.Genre, s.[Time], s.FilePath " +
                "FROM Song s INNER JOIN PlaylistSong ps ON ps.SongId = s.Id " +
                "WHERE ps.PlaylistId = ? ORDER BY ps.Id";

        List<Song> songs = new ArrayList<>();

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
        } catch (SQLException ex) {
            throw new Exception("Could not get songs for the playlist", ex);
        }

        return songs;
    }

    @Override
    public Playlist createPlaylist(Playlist playlist) throws Exception {

        String sql = "INSERT INTO Playlist (Name, SongCount, PLAYTIME) VALUES (?,?,?)";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, playlist.getName());
            stmt.setInt(2, playlist.getNumberOfSongs());
            stmt.setDouble(3, playlist.getTotalTime());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();

            int id = 0;

            if (rs.next()) {
                id = rs.getInt(1);
            }

            return new Playlist(
                    id,
                    playlist.getName(),
                    playlist.getNumberOfSongs(),
                    playlist.getTotalTime()
            );
        }
    }

    @Override
    public void updatePlaylist(Playlist playlist) throws Exception {

        String sql = "UPDATE Playlist SET Name = ?, SongCount = ?, PLAYTIME = ? WHERE Id = ? ";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, playlist.getName());
            stmt.setInt(2, playlist.getNumberOfSongs());
            stmt.setDouble(3, playlist.getTotalTime());
            stmt.setInt(4, playlist.getId());

            stmt.executeUpdate();
        }
    }

    @Override
    public void deletePlaylist(Playlist playlist) throws Exception {

        String sql = "DELETE FROM Playlist WHERE Id = ?";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, playlist.getId());
            stmt.executeUpdate();
        }
    }
}