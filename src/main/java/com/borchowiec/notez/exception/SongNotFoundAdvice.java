package com.borchowiec.notez.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Handles {@link SongNotFoundException}.
 */
@ControllerAdvice
public class SongNotFoundAdvice {

    /**
     * Handles throwing {@link SongNotFoundException} by returning 404 Http Status, and message.
     * @param e Exception
     * @return Message of exception.
     */
    @ResponseBody
    @ExceptionHandler(SongNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(SongNotFoundException e) {
        return e.getMessage();
    }
}
