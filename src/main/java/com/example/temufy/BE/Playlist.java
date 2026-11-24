package com.example.temufy.BE;

import java.util.List;

public class Playlist {

    private String name;
    private List<Song> numberOfSongs;
    private double totalTime;

    Playlist(String name, List<Song> numberOfSongs, double totalTime){
        this.name = name;
        this.numberOfSongs = numberOfSongs;
        this.totalTime = totalTime;
    }

    public String getName() {
        return name;
    }
    public double getTotalTime() {
        return totalTime;
    }
    public List<Song> getNumberOfSongs() {
        return numberOfSongs;
    }
}
