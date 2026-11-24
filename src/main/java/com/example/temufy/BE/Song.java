package com.example.temufy.BE;

public class Song {

    private String title;
    private String artist;
    private String genre;
    private double time;
    private int id;

    // For songs that already exist (loaded from the database)
    public Song(String title, String artist, String genre, double time, int id) {
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.time = time;
        this.id = id;
    }

    // For new songs, the database creates the id
    public Song(String title, String artist, String genre, double time) {
        this(title, artist, genre, time, 0);
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return title + " - " + artist + " (" + genre + ") " + time;
    }
}