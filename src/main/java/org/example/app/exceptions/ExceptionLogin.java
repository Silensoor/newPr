package org.example.app.exceptions;

public class ExceptionLogin extends Exception {
    private final String message;

    public ExceptionLogin(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

