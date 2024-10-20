package me.zedaster.articleservice.service;

/**
 * Exception thrown when an error occurs with {@link ArticleService}.
 */
public class ArticleServiceException extends Exception {
    public ArticleServiceException(String message) {
        super(message);
    }
}
