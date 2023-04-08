package org.example.app.exceptions;

public class ExceptionSyntax extends Exception {
    private final String message;

    public ExceptionSyntax(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}