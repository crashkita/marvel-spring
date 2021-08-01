package com.den.marvel.marvel.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MyFileNotFoundException extends RuntimeException{
    public MyFileNotFoundException(String s) {
        super(s);
    }

    public MyFileNotFoundException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
