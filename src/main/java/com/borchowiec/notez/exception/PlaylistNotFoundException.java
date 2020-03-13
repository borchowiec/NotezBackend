package com.borchowiec.notez.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PlaylistNotFoundException extends RuntimeException {

    private static final String message = "Not found playlist of id: ";

    public PlaylistNotFoundException(long playlistId) {
        super(message + playlistId);
    }

    public PlaylistNotFoundException(long playlistId, Throwable cause) {
        super(message + playlistId, cause);
    }
}
