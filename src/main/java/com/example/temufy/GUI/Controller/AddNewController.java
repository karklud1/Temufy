package com.example.temufy.GUI.Controller;
import com.example.temufy.BE.Song;
import com.mpatric.mp3agic.Mp3File;
import javafx.stage.FileChooser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.spi.AudioFileReader;
import java.io.File;


public class AddNewController {
    private File selectedFile;
    private int durationInSeconds;
    Song song;

    //FXML variables
    @FXML
    private TextField txtTitle;
    @FXML
    private TextField txtArtist;
    @FXML
    private ChoiceBox<String> cboxGenre;
    @FXML
    private TextField txtTime;
    @FXML
    private TextField txtFile;
    @FXML
    private Button btnFile;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnSave;

    @FXML
    public void initialize(){
        cboxGenre.getItems().addAll("POP","Rock","RnB & Soul","Hip-Hop & Rap","Electronic & Dance","Classical","Country","Reggae","Jazz", "Blues","Folk","Latin");
        cboxGenre.setValue("Genre...");
    }
    @FXML
    public void handleClickCancel(ActionEvent actionEvent){
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
    @FXML
    public void handleClickSave(ActionEvent actionEvent){
        txtTitle.getText();
        txtArtist.getText();
        Double.parseDouble(txtTime.getText());
        cboxGenre.getValue();
        System.out.print("\nThe title of the song is " + song.getTitle() + "\nThe artist is " + song.getArtist() + "\nThe song is " + song.getTime() + " minutes long" + "\nThe genre is " + song.getGenre());
    }
    @FXML
    public void handleClickChoose(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Audio File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Audio Files (*.mp3, *.wav)", "*.mp3", "*.wav")
        );

        Stage stage = (Stage) btnFile.getScene().getWindow();
        selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            txtFile.setText(selectedFile.getName());

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
            } catch (Exception e) {
                e.printStackTrace();
                txtTime.setText("0:00");
            }
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
