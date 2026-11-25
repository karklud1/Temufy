package com.example.temufy.BE;

public class Song {

    private int id;
    private String title;
    private String artist;
    private String genre;
    private double time;        // længde i minutter (3.5 = 3:30)
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

    // Bruges af tabelkolonnen til at vise 3:45 i stedet for 3.75
    public String getTimeFormatted() {
        int totalSeconds = (int) Math.round(time * 60);
        return String.format("%d:%02d", totalSeconds / 60, totalSeconds % 60);
    }

    @Override
    public String toString() {
        return id + ": " + title + " - " + artist + " (" + getTimeFormatted() + ")";
    }
}