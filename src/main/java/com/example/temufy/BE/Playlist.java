package com.example.temufy.BE;

public class Playlist {

    private int id;
    private String name;
    private int numberOfSongs;
    private double totalTime;

    public Playlist(int id,String name,int numberOfSongs,double totalTime){
        this.id = id;
        this.name = name;
        this.numberOfSongs = numberOfSongs;
        this.totalTime = totalTime;
    }

    public String getName() {return name;}
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
