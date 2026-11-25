package com.example.temufy.GUI.Model;

import com.example.temufy.BE.Song;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

// Plays a song from the mp3 folder. MediaPlayer is JavaFX, so this stays in the GUI.
public class PlayerModel {

    private MediaPlayer mediaPlayer;
    private List<Song> queue = new ArrayList<>();
    private int currentIndex = -1;
    private boolean loop;
    private double volume = 0.8;

    private Runnable onSongChanged;
    private BiConsumer<Double, Double> onTimeChanged;
    private Consumer<Exception> onError;

    public void setOnSongChanged(Runnable onSongChanged) {
        this.onSongChanged = onSongChanged;
    }

    public void setOnTimeChanged(BiConsumer<Double, Double> onTimeChanged) {
        this.onTimeChanged = onTimeChanged;
    }

    public void setOnError(Consumer<Exception> onError) {
        this.onError = onError;
    }

    public Song getCurrentSong() {
        if (currentIndex < 0 || currentIndex >= queue.size()) {
            return null;
        }
        return queue.get(currentIndex);
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING;
    }

    // Copies the list that was clicked. loop is true for a playlist.
    public void playSong(Song song, List<Song> songs, boolean loop) throws Exception {
        this.loop = loop;
        queue = new ArrayList<>(songs);
        currentIndex = queue.indexOf(song);
        if (currentIndex < 0) {
            queue.clear();
            queue.add(song);
            currentIndex = 0;
        }
        startCurrentSong();
    }

    public void togglePlayPause() {
        if (mediaPlayer == null) {
            return;
        }
        if (isPlaying()) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.play();
        }
    }

    // Next song. A playlist goes back to the first song at the end.
    public void nextSong() {
        if (queue.isEmpty()) {
            return;
        }
        if (currentIndex < queue.size() - 1) {
            currentIndex++;
        } else if (loop) {
            currentIndex = 0;
        } else {
            return;
        }
        try {
            startCurrentSong();
        } catch (Exception e) {
            report(e);
        }
    }

    // More than 3 seconds in: restart this song. Otherwise go to the song before.
    public void previousSong() {
        if (mediaPlayer != null && mediaPlayer.getCurrentTime().toSeconds() > 3) {
            mediaPlayer.seek(Duration.ZERO);
            return;
        }
        if (currentIndex > 0) {
            currentIndex--;
        } else if (loop && !queue.isEmpty()) {
            currentIndex = queue.size() - 1;
        } else if (mediaPlayer != null) {
            mediaPlayer.seek(Duration.ZERO);
            return;
        } else {
            return;
        }
        try {
            startCurrentSong();
        } catch (Exception e) {
            report(e);
        }
    }

    public void seek(double seconds) {
        if (mediaPlayer != null) {
            mediaPlayer.seek(Duration.seconds(seconds));
        }
    }

    public void setVolume(double volume) {
        this.volume = volume;
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume);
        }
    }

    // Opens the file from the song path. The file is not read from the database.
    private void startCurrentSong() throws Exception {
        Song song = getCurrentSong();
        if (song == null || song.getFilePath() == null || song.getFilePath().isBlank()) {
            throw new Exception("This song has no audio file.");
        }

        File file = new File(song.getFilePath());
        if (!file.exists()) {
            throw new Exception("Could not find the audio file for " + song.getTitle());
        }

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }

        Media media = new Media(file.toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setVolume(volume);
        mediaPlayer.setOnReady(this::notifyTime);
        mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> notifyTime());
        mediaPlayer.setOnPlaying(this::notifySongChanged);
        mediaPlayer.setOnPaused(this::notifySongChanged);
        mediaPlayer.setOnStopped(this::notifySongChanged);
        mediaPlayer.setOnEndOfMedia(this::playNextOrStop);
        mediaPlayer.setOnError(() -> report(new Exception("Could not play " + song.getTitle())));
        mediaPlayer.play();
        notifySongChanged();
    }

    private void playNextOrStop() {
        if (queue.isEmpty()) {
            return;
        }
        if (currentIndex < queue.size() - 1) {
            currentIndex++;
        } else if (loop) {
            currentIndex = 0;
        } else {
            notifySongChanged();
            return;
        }
        try {
            startCurrentSong();
        } catch (Exception e) {
            report(e);
        }
    }

    private void notifySongChanged() {
        if (onSongChanged != null) {
            onSongChanged.run();
        }
    }

    private void notifyTime() {
        if (onTimeChanged == null || mediaPlayer == null) {
            return;
        }
        double current = mediaPlayer.getCurrentTime().toSeconds();
        double total = mediaPlayer.getMedia().getDuration().toSeconds();
        if (Double.isNaN(total) || Double.isInfinite(total) || total < 0) {
            total = 0;
        }
        if (Double.isNaN(current) || current < 0) {
            current = 0;
        }
        onTimeChanged.accept(current, total);
    }

    private void report(Exception exception) {
        if (onError != null) {
            onError.accept(exception);
        }
    }
}
