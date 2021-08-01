package com.den.marvel.marvel.exception;

import java.util.function.Supplier;

public class CharacterNotFoundException extends RuntimeException {
    public CharacterNotFoundException(String s) {
        super(s);
    }

    public CharacterNotFoundException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
