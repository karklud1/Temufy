package com.example.temufy.BE;

public class Song {

    private String title;
    private String artist;
    private String genre;
    private double time;

    public Song(String title, String artist, String genre, double time){
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.time = time;
    }

    public String getTitle(){
        return title;
    }
    public String getArtist(){
        return artist;
    }
    public double getTime(){
        return time;
    }
    public String getGenre(){
        return genre;
    }
}
