package me.zedaster.articleservice.dto.article;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.zedaster.articleservice.entity.ArticleInfo;

import java.time.Instant;
import java.util.List;

/**
 * Summary of some article
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode(of = "id")
public class ArticleSummary {
    /**
     * Article ID
     */
    private long id;

    /**
     * Title of the article
     */
    private String title;

    /**
     * Date of creation of the article
     */
    private Instant createdAt;

    /**
     * Data of creator of the article
     */
    private CreatorData creatorData;

    public static List<ArticleSummary> fromArticleInfos(List<ArticleInfo> articleInfos) {
        return articleInfos.stream().map(ArticleSummary::fromArticleInfo).toList();
    }

    private static ArticleSummary fromArticleInfo(ArticleInfo articleInfo) {
        return new ArticleSummary(
                articleInfo.getId(),
                articleInfo.getTitle(),
                articleInfo.getCreatedAt(),
                articleInfo.getCreator().toCreatorData()
        );
    }
}
