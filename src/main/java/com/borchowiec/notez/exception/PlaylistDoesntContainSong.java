package com.borchowiec.notez.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PlaylistDoesntContainSong extends RuntimeException {

    public PlaylistDoesntContainSong(long playlistId, long songId) {
        super("Playlist of id: " + playlistId + " doesn't contain song of id: " + songId);
    }

    public PlaylistDoesntContainSong(long playlistId, long songId, Throwable cause) {
        super("Playlist of id: " + playlistId + " doesn't contain song of id: " + songId, cause);
    }
}
