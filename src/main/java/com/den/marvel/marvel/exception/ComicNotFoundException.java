package com.den.marvel.marvel.exception;

public class ComicNotFoundException extends RuntimeException {
    public ComicNotFoundException(String s) {
        super(s);
    }

    public ComicNotFoundException(String s, Throwable throwable) {
        super(s, throwable);
    }
}