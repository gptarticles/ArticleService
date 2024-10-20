package me.zedaster.articleservice.service;

/**
 * Exception that is thrown when an error occurs when the app is working. The message and the cause won't be shown
 * to the user.
 */
public class InternalServerException extends RuntimeException {
    public InternalServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
