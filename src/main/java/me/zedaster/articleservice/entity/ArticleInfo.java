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
@Table(name = "articles")
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
     * User id of creator of the article
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "creatorId", referencedColumnName = "creatorId", nullable = false)
    private Creator creator;

    public ArticleInfo(String title, Instant createdAt, Creator creator) {
        this.title = title;
        this.createdAt = createdAt;
        this.creator = creator;
    }

    private ArticleInfo(ArticleInfo articleInfo) {
        this.id = articleInfo.id;
        this.title = articleInfo.title;
        this.createdAt = articleInfo.createdAt;
        this.creator = articleInfo.creator;
    }

    public ArticleInfo copy() {
        return new ArticleInfo(this);
    }
}
