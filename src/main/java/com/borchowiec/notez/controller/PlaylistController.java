package com.borchowiec.notez.controller;

import com.borchowiec.notez.exception.*;
import com.borchowiec.notez.model.Playlist;
import com.borchowiec.notez.model.Song;
import com.borchowiec.notez.model.User;
import com.borchowiec.notez.payload.AddSongToPlaylistRequest;
import com.borchowiec.notez.payload.PlaylistResponse;
import com.borchowiec.notez.payload.PlaylistsResponse;
import com.borchowiec.notez.payload.AddSongToPlaylistResponse;
import com.borchowiec.notez.repository.PlaylistRepository;
import com.borchowiec.notez.repository.SongRepository;
import com.borchowiec.notez.repository.UserRepository;
import com.borchowiec.notez.service.PlaylistService;
import javassist.NotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
public class PlaylistController {
    private final UserRepository userRepository;
    private final PlaylistService playlist;
    private final PlaylistRepository playlistRepository;
    private final SongRepository songRepository;

    public PlaylistController(UserRepository userRepository, PlaylistService playlist, PlaylistRepository playlistRepository, SongRepository songRepository) {
        this.userRepository = userRepository;
        this.playlist = playlist;
        this.playlistRepository = playlistRepository;
        this.songRepository = songRepository;
    }

    @PostMapping("/playlist")
    public Playlist createPlaylist(@RequestBody Map<String, String> request, Principal principal) {
        String username = principal.getName();
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Not found user of username: " + username));

        return playlist.createPlaylist(request.get("playlistName"), user.getId());
    }

    @DeleteMapping("/playlist/{playlistId}")
    public void removePlaylist(@PathVariable long playlistId, Principal principal) throws NotFoundException {
        Playlist playlist = playlistRepository
                                .findById(playlistId)
                                .orElseThrow(() -> new PlaylistNotFoundException(playlistId));

        User user = userRepository
                        .findByUsername(principal.getName())
                        .orElseThrow(() -> new NotFoundException("Not found user of username: " + principal.getName()));

        if (playlist.getOwner() != user.getId()) {
            String message = "Playlist of id " + playlistId + " doesn't belong to user of id " + user.getId();
            throw new NotOwnerException(message);
        }

        playlistRepository.delete(playlist);
    }

    @GetMapping("/playlist/{playlistId}")
    public PlaylistResponse getPlaylist(@PathVariable long playlistId, Principal principal) throws NotFoundException {
        Playlist playlist = playlistRepository
                                    .findById(playlistId)
                                    .orElseThrow(() -> new PlaylistNotFoundException(playlistId));

        boolean isOwner = false;
        if (principal != null) {
            User user = userRepository
                    .findByUsername(principal.getName())
                    .orElseThrow(() -> new NotFoundException("Not found user of username: " + principal.getName()));

            isOwner = user.getId() == playlist.getOwner();
        }

        PlaylistResponse playlistResponse = new PlaylistResponse();
        playlistResponse.setPlaylist(playlist);
        playlistResponse.setOwner(isOwner);

        return playlistResponse;
    }

    @GetMapping("/playlists/{userId}")
    public PlaylistsResponse getUsersPlaylists(@PathVariable long userId, Principal principal) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Not found user of id: " + userId));

        List<Playlist> playlists = playlistRepository.findByOwner(userId);

        boolean isOwner = false;
        if (principal != null) {
            isOwner = user.getUsername().equals(principal.getName());
        }

        PlaylistsResponse playlistsResponse = new PlaylistsResponse();
        playlistsResponse.setOwner(isOwner);
        playlistsResponse.setPlaylists(playlists);

        return playlistsResponse;
    }

    @PutMapping("/playlist")
    @Transactional
    public AddSongToPlaylistResponse addSongToPlaylist(@RequestBody AddSongToPlaylistRequest request,
                                                       Principal principal) {
        long playlistId = request.getPlaylistId();
        Playlist playlist = playlistRepository
                .findById(playlistId).orElseThrow(() -> new PlaylistNotFoundException(playlistId));

        long songId = request.getSongId();
        Song song = songRepository.findById(songId).orElseThrow(() -> new SongNotFoundException(songId));

        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Not found user of username: " + username));

        if (user.getId() != playlist.getOwner()) {
            throw new NotOwnerException("Currently logged in user is not a owner of playlist.");
        }

        if (playlist.getSongs().contains(song)) {
            throw new SongAlreadyExistsInPlaylistException(songId, playlistId);
        }

        playlist.getSongs().add(song);

        AddSongToPlaylistResponse response = new AddSongToPlaylistResponse();
        response.setSong(song);
        response.setSongIndex(playlist.getSongs().indexOf(song));
        response.setPlaylistId(playlistId);
        return response;
    }

    // todo remove song from playlist
}
