package com.example.temufy.DAL.db;

import com.example.temufy.BE.Song;
import com.example.temufy.DAL.ISongDataAccess;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SongDAO_DB implements ISongDataAccess {

    private DBConnector dbConnector = new DBConnector();

    public SongDAO_DB() throws IOException {
    }

    @Override
    public List<Song> getAllSongs() throws Exception {

        List<Song> songs = new ArrayList<>();

        try (Connection conn = dbConnector.getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery("SELECT * FROM Song");

            while (rs.next()) {
                int id = rs.getInt("Id");
                String title = rs.getString("Title");
                String artist = rs.getString("Artist");
                String genre = rs.getString("Genre");
                double time = rs.getDouble("Time");

                songs.add(new Song(title, artist, genre, time, id));
            }
        }

        return songs;
    }

    @Override
    public Song createSong(Song song) throws Exception {
        String sql = "INSERT INTO Song (Title,Artist,Genre,[Time]) VALUES (?,?,?,?);";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, song.getTitle());
            stmt.setString(2, song.getArtist());
            stmt.setString(3, song.getGenre());
            stmt.setDouble(4, song.getTime());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            int id = 0;

            if (rs.next()) {
                id = rs.getInt(1);
            }

            return new Song(song.getTitle(), song.getArtist(), song.getGenre(), song.getTime(), id);
        }
    }

    @Override
    public void updateSong(Song song) throws Exception {
        String sql = "UPDATE Song SET Title = ?, Artist = ?, Genre = ?, [Time] = ? WHERE Id = ?";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, song.getTitle());
            stmt.setString(2, song.getArtist());
            stmt.setString(3, song.getGenre());
            stmt.setDouble(4, song.getTime());
            stmt.setInt(5, song.getId());

            stmt.executeUpdate();
        }
    }

    @Override
    public void deleteSong(Song song) throws Exception {
        String sql = "DELETE FROM Song WHERE Id = ?";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, song.getId());
            stmt.executeUpdate();
        }
    }
}