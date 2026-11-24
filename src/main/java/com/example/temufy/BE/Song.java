package com.example.temufy.BE;

public class Song {

    private int id;
    private String title;
    private String artist;
    private String genre;
    private int time; // length in seconds

    public Song(int id, String title, String artist, String genre, int time) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.time = time;
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

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    // Returns the time as m:ss, e.g. 225 -> "3:45"
    public String getFormattedTime() {
        return String.format("%d:%02d", time / 60, time % 60);
    }

    @Override
    public String toString() {
        return id + ": " + title + " - " + artist + " (" + genre + ") [" + getFormattedTime() + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Song song = (Song) o;
        return id == song.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}