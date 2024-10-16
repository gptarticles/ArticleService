package me.zedaster.articleservice.service;

import me.zedaster.articleservice.dto.ArticleData;
import me.zedaster.articleservice.model.Article;
import me.zedaster.articleservice.model.ArticleSummary;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for handling operations with articles
 */
@Service
public class ArticleService {
    /**
     * Gets a certain article from a storage
     * @param id ID of the article
     * @return Article object
     */
    public Article getArticle(long id) {
        // TODO
        return null;
    }

    /**
     * Gets summaries of articles that has been published recently
     * @param pageNumber Number of page
     * @return List of article summaries
     */
    public List<ArticleSummary> getRecentArticleSummaries(int pageNumber) {
        // TODO
        return null;
    }

    /**
     * Gets summaries of articles that published by user with the specified ID.
     * @param userId specified ID of the user
     * @return List of article summaries
     */
    public List<ArticleSummary> getArticleSummariesByUserId(long userId) {
        // TODO
        return null;
    }

    /**
     * Creates a new article
     * @param userId ID of user that is creating the article
     * @param articleData Data for the new article
     * @return ID of the new article
     */
    public long createArticle(long userId, ArticleData articleData) throws IllegalArgumentException {
        // TODO
        return 0L;
    }

    /**
     * Updates an existing article
     * @param userId ID of the user that is updating the article
     * @param articleId ID of the article
     * @param articleData New data for the article
     */
    public void updateArticle(long userId, long articleId, ArticleData articleData) throws IllegalArgumentException {
        // TODO
        // TODO check if the user can edit the article (if the user own the article)
    }
}
