package com.example.temufy.GUI.Model;
import com.mpatric.mp3agic.Mp3File;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javafx.scene.control.TextField;
import java.io.File;

public class SongTime {
    private int durationInSeconds;
    public void songTime(File selectedFile, TextField txtTime, TextField txtTitle){
        try {
                durationInSeconds = getDurationInSeconds(selectedFile);

                // Format as minutes:seconds (e.g., 3:05) for the UI text field
                int minutes = durationInSeconds / 60;
                int seconds = durationInSeconds % 60;
                txtTime.setText(String.format("%d:%02d", minutes, seconds));

                // Optional: Auto-fill title from filename (without .mp3/.wav) if txtTitle is empty
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
    public int getDurationInSeconds(File file) throws Exception {
        String name = file.getName().toLowerCase();

        if (name.endsWith(".mp3")) {
            Mp3File mp3 = new Mp3File(file);
            return (int) mp3.getLengthInSeconds();
        } else if (name.endsWith(".wav")) {
            try (AudioInputStream stream = AudioSystem.getAudioInputStream(file)) {
                AudioFormat format = stream.getFormat();
                long frames = stream.getFrameLength();
                return (int) Math.round((frames + 0.0) / format.getFrameRate());
            }
        } else {
            throw new IllegalArgumentException("Unsupported audio format: " + name);
        }
    }

}


