package com.example.temufy.DAL.db;

import com.example.temufy.BE.Playlist;
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
                int songs = rs.getInt("Songs");
                int time = rs.getInt("Time");

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

    @Override
    public Playlist createPlaylist(Playlist playlist) throws Exception {

        String sql = "INSERT INTO Playlist (Name, NumberOfSongs, TotalTime) VALUES (?,?,?)";

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
                    playlist.getId(),
                    playlist.getName(),
                    playlist.getNumberOfSongs(),
                    playlist.getTotalTime()
            );
        }
    }

    @Override
    public void updatePlaylist(Playlist playlist) throws Exception {

        String sql = "UPDATE Playlist SET Name = ?, NumberOfSongs = ?, TotalTime = ? WHERE Id = ? ";

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