package com.borchowiec.notez.exception;

public class SongNotFoundException extends RuntimeException {
    /**
     * Is thrown when song of given id doesn't exists.
     * @param id Id of song that doesn't exists.
     */
    public SongNotFoundException(long id) {
        super("Not found song of id: " + id);
    }
}