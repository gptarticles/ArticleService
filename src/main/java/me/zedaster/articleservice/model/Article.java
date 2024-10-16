package me.zedaster.articleservice.model;

import java.util.Date;

/**
 * Model of full article
 */
public class Article {
    /**
     * Article ID
     */
    private Long id;

    /**
     * Title of the article
     */
    private String title;

    /**
     * String content of the article
     */
    private String content;

    /**
     * Date of creation of the article
     */
    private Date createdAt;

    /**
     * User id of creator of the article
     */
    private Long creatorId;

    /**
     * Nickname of creator of the article
     */
    private String creatorName;
}
