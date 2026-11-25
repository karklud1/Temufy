package com.example.temufy.BE;

public class Song {

    private int id;
    private String title;
    private String artist;
    private String genre;
    private double time;        // length in seconds. 277 = 04:37
    private String filePath;    // sti til lydfilen

    // For songs loaded from the database (the id already exists)
    public Song(String title, String artist, String genre, double time, String filePath, int id) {
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.time = time;
        this.filePath = filePath;
        this.id = id;
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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    // The song table asks for "timeFormatted", so 277 shows as 04:37
    public String getTimeFormatted() {
        int totalSeconds = (int) Math.round(time);
        if (totalSeconds < 0) {
            totalSeconds = 0;
        }
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    public String toString() {
        return id + ": " + title + " - " + artist + " (" + getTimeFormatted() + ")";
    }
}