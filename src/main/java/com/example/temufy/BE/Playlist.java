package com.example.temufy.BE;

public class Playlist {

    private String name;
    private int id;
    private int numberOfSongs;
    private double totalTime;

    public Playlist(String name, int id){
        this.name = name;
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
