package me.zedaster.articleservice.service.content;

import java.util.Optional;

/**
 * Service for working with content of the articles
 */
public interface ContentService {
    /**
     * Fetch content by article ID
     * @param articleId ID of the article
     * @return String representation of article content
     */
    Optional<String> getContentByArticleId(long articleId);

    /**
     * Save content for the article
     * @param articleId ID of the article
     * @param content Content to save
     */
    void saveContent(long articleId, String content);
}
