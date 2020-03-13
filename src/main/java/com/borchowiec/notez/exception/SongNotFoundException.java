package com.borchowiec.notez.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SongNotFoundException extends RuntimeException {
    /**
     * Is thrown when song of given id doesn't exists.
     * @param id Id of song that doesn't exists.
     */
    public SongNotFoundException(long id) {
        super("Not found song of id: " + id);
    }
}