package com.borchowiec.notez.controller;

import com.borchowiec.notez.model.Playlist;
import com.borchowiec.notez.model.User;
import com.borchowiec.notez.repository.UserRepository;
import com.borchowiec.notez.service.PlaylistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
public class PlaylistController {
    private final UserRepository userRepository;
    private final PlaylistService playlist;

    public PlaylistController(UserRepository userRepository, PlaylistService playlist) {
        this.userRepository = userRepository;
        this.playlist = playlist;
    }

    @PostMapping("/playlist")
    public Playlist createPlaylist(@RequestBody Map<String, String> request, Principal principal) {
        String username = principal.getName();
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Not found user of username: " + username));

        return playlist.createPlaylist(request.get("playlistName"), user.getId());
    }

    //todo remove playlist
    //todo get playlist
    //todo get playlists
    //todo add song to playlist
    //todo remove song from playlist
}
