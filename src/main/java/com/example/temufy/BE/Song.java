package com.example.temufy.BE;

import java.io.File;

public class Song {

    private String title;
    private String artist;
    private String genre;
    private String time;
    private File file;

    public Song(String title, String artist, String genre, String time, File file){
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.time = time;
        this.file = file;
    }

    public String getTitle(){
        return title;
    }
    public String getArtist(){
        return artist;
    }
    public String getTime(){
        return time;
    }
    public String getGenre(){
        return genre;
    }
    public File getFile(){
        return file;
    }
}
