package org.example.app.exceptions;


public class UploadFilesException extends Exception {
    private final String message;

    public UploadFilesException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

