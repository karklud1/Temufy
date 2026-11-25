package com.example.temufy.BE;

public class Playlist {

    private String name;
    private int numberOfSongs;
    private double totalTime;
    private int id;

    public Playlist(String name, int numberOfSongs, double totalTime, int id){
        this.name = name;
        this.numberOfSongs = numberOfSongs;
        this.totalTime = totalTime;
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public double getTotalTime() {
        return totalTime;
    }
    public int getNumberOfSongs() {
        return numberOfSongs;
    }
    public int getId(){
        return id;
    }
}
