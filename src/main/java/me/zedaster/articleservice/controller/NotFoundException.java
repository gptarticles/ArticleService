package me.zedaster.articleservice.controller;

/**
 * Exception thrown when an entity is not found.
 * {@link GlobalExceptionHandler} will catch this exception and return a 404 status code.
 */
public class NotFoundException extends Exception {
    public NotFoundException(String message) {
        super(message);
    }
}
