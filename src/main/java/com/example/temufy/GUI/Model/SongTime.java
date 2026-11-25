package com.example.temufy.GUI.Model;
import com.mpatric.mp3agic.Mp3File;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javafx.scene.control.TextField;
import java.io.File;

//Class for automatically grabbing time of song from the provided file
public class SongTime {
    private int durationInSeconds;
    //Given parameters File, and two text fields, inserts time and name from the file into the GUI.
    public void songTime(File selectedFile, TextField txtTime, TextField txtTitle){
        try {
                durationInSeconds = getDurationInSeconds(selectedFile);

                // Format as minutes:seconds (e.g., 3:05) for the UI text field
                int minutes = durationInSeconds / 60;
                int seconds = durationInSeconds % 60;
                txtTime.setText(String.format("%d:%02d", minutes, seconds));

                // Autofill title from filename (without .mp3/.wav) if txtTitle is empty
                if (txtTitle.getText().isBlank()) {
                    String fileName = selectedFile.getName();
                    int dotIndex = fileName.lastIndexOf('.');
                    txtTitle.setText(dotIndex > 0 ? fileName.substring(0, dotIndex) : fileName);
                }
        }
        catch (Exception e)
        {
                e.printStackTrace();
                txtTime.setText("0:00");
        }
    }
    //Helper method for retrieving the time from the file
    public int getDurationInSeconds(File file) throws Exception {
        String name = file.getName().toLowerCase();
        // Check if the file is an MP3 based on its extension
        if (name.endsWith(".mp3")) {
            // Load the file using a third-party Mp3File library (mp3agic)
            Mp3File mp3 = new Mp3File(file);
            // Return the total duration in seconds, cast from long to int
            return (int) mp3.getLengthInSeconds();
            // Check if the file is a WAV based on its extension
        } else if (name.endsWith(".wav")) {
            // Open an audio stream using try-with-resources to ensure it closes automatically
            try (AudioInputStream stream = AudioSystem.getAudioInputStream(file)) {
                AudioFormat format = stream.getFormat();
                long frames = stream.getFrameLength();
                // Calculate duration: (total frames / frames per second), rounded to the nearest whole second
                // Adding 0.0 forces floating-point division to preserve decimals before rounding
                return (int) Math.round((frames + 0.0) / format.getFrameRate());
            }
            // Handle any file formats other than .mp3 or .wav
        } else {
            throw new IllegalArgumentException("Unsupported audio format: " + name);
        }
    }

}


