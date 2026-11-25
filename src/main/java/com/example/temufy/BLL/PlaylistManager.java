
package com.example.temufy.BLL;

import com.example.temufy.BE.Playlist;
import com.example.temufy.BE.Song;
import com.example.temufy.DAL.IPlaylistDataAccess;
import com.example.temufy.DAL.db.PlaylistDAO_DB;

import java.io.IOException;
import java.util.List;

public class PlaylistManager {

    private IPlaylistDataAccess playlistDAO;

    public PlaylistManager() throws IOException {
        playlistDAO = new PlaylistDAO_DB();
    }

    public List<Playlist> getAllPlaylists() throws Exception {
        return playlistDAO.getAllPlaylists();
    }

    public Playlist createPlaylist(String name) throws Exception {
        return playlistDAO.createPlaylist(name);
    }

    public void updatePlaylist(Playlist updatedPlaylist) throws Exception {
        playlistDAO.updatePlaylist(updatedPlaylist);
    }

    public void deletePlaylist(Playlist playlistToBeDeleted) throws Exception {
        playlistDAO.deletePlaylist(playlistToBeDeleted);
    }

    // Henter alle sange fra en bestemt playlist
    public List<Song> getSongsFromPlaylist(int playlistId) throws Exception {
        return playlistDAO.getSongsOnPlaylist(playlistId);
    }

    // Tilføjer en sang til en playlist
    public void addSongToPlaylist(int playlistId, int songId) throws Exception {
        playlistDAO.addSongToPlaylist(playlistId, songId);
    }

    // Fjerner en sang fra en playlist
    public void removeSongFromPlaylist(int playlistId, int songId) throws Exception {
        playlistDAO.removeSongFromPlaylist(playlistId, songId);
    }

    // Flytter en sang én position op
    public void moveSongUp(int playlistId, int songId) throws Exception {
        playlistDAO.moveSongUp(playlistId, songId);
    }

    // Flytter en sang én position ned
    public void moveSongDown(int playlistId, int songId) throws Exception {
        playlistDAO.moveSongDown(playlistId, songId);
    }
}
