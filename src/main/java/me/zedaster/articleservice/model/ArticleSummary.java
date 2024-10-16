package me.zedaster.articleservice.model;

import java.util.Date;

/**
 * Summary of some article
 */
public class ArticleSummary {
    /**
     * Article ID
     */
    private Long id;

    /**
     * Title of the article
     */
    private String title;

    /**
     * Date of creation of the article
     */
    private Date createdAt;

    /**
     * Nickname of creator of the article
     */
    private String creatorName;
}
