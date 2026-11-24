package com.example.temufy.BLL.util;

import com.example.temufy.BE.Playlist;
import java.util.ArrayList;
import java.util.List;

public class PlaylistSearcher {

    public List<Playlist> search(List<Playlist> searchBase, String query) {
        List<Playlist> searchResult = new ArrayList<>();

        for (Playlist playlist : searchBase) {
            if (compareToSongTitle(query, playlist)) {
                searchResult.add(playlist);
            }
        }

        return searchResult;
    }

    private boolean compareToSongTitle(String query, Playlist playlist) {
        return playlist.getName().toLowerCase().contains(query.toLowerCase());
    }

}