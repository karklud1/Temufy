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
    public void setName(String name) { this.name = name; }

    // The playlist table asks for "totalTimeFormatted", so 454 shows as 00:07:34
    public String getTotalTimeFormatted() {
        int totalSeconds = (int) Math.round(totalTime);
        if (totalSeconds < 0) {
            totalSeconds = 0;
        }
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
    public void setNumberOfSongs(int numberOfSongs){
        this.numberOfSongs = numberOfSongs;
    }
    public void setTotalTime(double totalTime){
        this.totalTime = totalTime;
    }
}
