package com.borchowiec.notez.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class NotOwnerException extends RuntimeException {
    public NotOwnerException(String msg) {
        super(msg);
    }

    public NotOwnerException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
