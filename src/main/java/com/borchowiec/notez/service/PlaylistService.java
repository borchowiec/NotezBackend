package com.borchowiec.notez.service;

import com.borchowiec.notez.model.Playlist;
import com.borchowiec.notez.repository.PlaylistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;

@Service
public class PlaylistService {

    private final PlaylistRepository playlistRepository;

    public PlaylistService(PlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
    }

    public Playlist createPlaylist(String playlistName, long ownerId) {
        Playlist playlist = new Playlist();
        playlist.setName(playlistName);
        playlist.setOwner(ownerId);
        playlist.setSongs(new LinkedList<>());

        playlistRepository.save(playlist);

        return playlist;
    }
}
