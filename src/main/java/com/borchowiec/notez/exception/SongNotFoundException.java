package com.borchowiec.notez.exception;

public class SongNotFoundException extends RuntimeException {
    public SongNotFoundException(long id) {
        super("Not found song of id: " + id);
    }
}