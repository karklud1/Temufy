package com.example.temufy.BE;

public class Song {

    private int id;
    private String title;
    private String artist;
    private int year;
    private int time; // length in seconds

    public Song(int id, String title, String artist, int year, int time) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.year = year;
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

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
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
        return id + ": " + title + " - " + artist + " (" + year + ") [" + getFormattedTime() + "]";
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