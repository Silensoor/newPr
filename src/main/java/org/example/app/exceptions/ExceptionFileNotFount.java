package org.example.app.exceptions;


public class ExceptionFileNotFount extends Exception {
    private final String message;

    public ExceptionFileNotFount(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

