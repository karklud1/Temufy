package com.example.temufy.GUI.Model;

import javafx.scene.control.Label;

public class TotalSongs {

    public void totalNumberSongs(Label label, SongModel songModel) {
        if (label != null && songModel != null) {
            int count = songModel.getObservableSongs().size();
            label.setText(count + (count == 1 ? " Song" : " Songs"));
        }
    }
}