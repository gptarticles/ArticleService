package me.zedaster.articleservice.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Row of article in the database
 */
@Entity
@Table(name = "articles", indexes = {
        @Index(name = "article_summaries_creator_id_index", columnList = "creatorId")
})
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
public class ArticleInfo {
    /**
     * Article ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * Title of the article
     */
    private String title;

    /**
     * UTC moment of creation of the article.
     */
    private Instant createdAt;

    /**
     * Creator of the article
     */
    private Long creatorId;

    public ArticleInfo(String title, Instant createdAt, Long creatorId) {
        this.title = title;
        this.createdAt = createdAt;
        this.creatorId = creatorId;
    }

    private ArticleInfo(ArticleInfo articleInfo) {
        this.id = articleInfo.id;
        this.title = articleInfo.title;
        this.createdAt = articleInfo.createdAt;
        this.creatorId = articleInfo.creatorId;
    }

    public ArticleInfo copy() {
        return new ArticleInfo(this);
    }
}
