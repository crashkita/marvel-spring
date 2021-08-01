package com.den.marvel.marvel.exception;

public class FileStorageException extends RuntimeException{
    public FileStorageException(String s) {
        super(s);
    }

    public FileStorageException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
