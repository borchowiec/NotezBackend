package com.borchowiec.notez.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class SongAlreadyExistsInPlaylistException extends RuntimeException {
    public SongAlreadyExistsInPlaylistException(long songId, long playlistId) {
        super("Song of id: " + songId + " already exists in playlist of id: " + playlistId);
    }
}