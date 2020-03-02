package com.borchowiec.notez.controller;

import com.borchowiec.notez.exception.NotOwnerException;
import com.borchowiec.notez.exception.PlaylistNotFoundException;
import com.borchowiec.notez.model.Playlist;
import com.borchowiec.notez.model.User;
import com.borchowiec.notez.repository.PlaylistRepository;
import com.borchowiec.notez.repository.UserRepository;
import com.borchowiec.notez.service.PlaylistService;
import javassist.NotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;

@RestController
public class PlaylistController {
    private final UserRepository userRepository;
    private final PlaylistService playlist;
    private final PlaylistRepository playlistRepository;

    public PlaylistController(UserRepository userRepository, PlaylistService playlist, PlaylistRepository playlistRepository) {
        this.userRepository = userRepository;
        this.playlist = playlist;
        this.playlistRepository = playlistRepository;
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
    //todo get playlist
    //todo get playlists
    //todo add song to playlist
    //todo remove song from playlist
}
