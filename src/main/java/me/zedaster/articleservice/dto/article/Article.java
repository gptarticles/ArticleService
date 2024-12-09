package me.zedaster.articleservice.dto.article;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.zedaster.articleservice.entity.ArticleInfo;

import java.time.Instant;

/**
 * Model of full article
 */
@Getter
@EqualsAndHashCode(of = "id")
public class Article {
    /**
     * Article ID
     */
    private final Long id;

    /**
     * Title of the article
     */
    private final String title;

    /**
     * String content of the article
     */
    private final String content;

    /**
     * Date of creation of the article
     */
    private final Instant createdAt;

    /**
     * Data of creator of the article
     */
    private final Creator creator;

    public Article(ArticleInfo articleInfo, String content, Creator creator) {
        this.id = articleInfo.getId();
        this.title = articleInfo.getTitle();
        this.content = content;
        this.createdAt = articleInfo.getCreatedAt();
        this.creator = creator;
    }
}
