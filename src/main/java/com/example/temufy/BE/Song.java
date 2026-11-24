package com.example.temufy.BE;

public class Song {

    private int id;
    private String title;
    private String artist;
    private String genre;
    private double time;

    // For songs loaded from the database (the id already exists)
    public Song(String title, String artist, String genre, double time, int id) {
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.time = time;
        this.id = id;
    }

    // For new songs (the database creates the id)
    public Song(String title, String artist, String genre, double time) {
        this(title, artist, genre, time, 0);
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getGenre() {
        return genre;
    }

    public double getTime() {
        return time;
    }
}