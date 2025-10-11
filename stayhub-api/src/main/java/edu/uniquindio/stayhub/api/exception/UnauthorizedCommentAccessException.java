package edu.uniquindio.stayhub.api.exception;

public class UnauthorizedCommentAccessException extends RuntimeException{
    public UnauthorizedCommentAccessException(String message) {
        super(message);
    }
}