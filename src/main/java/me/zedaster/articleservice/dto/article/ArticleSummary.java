package me.zedaster.articleservice.dto.article;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.zedaster.articleservice.entity.ArticleInfo;

import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;

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
    private Creator creator;

    public static List<ArticleSummary> fromArticleInfosBySingleCreator(List<ArticleInfo> articleInfos, Creator creator) {
        return articleInfos.stream()
                .map(articleInfo -> fromArticleInfoAndCreator(articleInfo, creator))
                .toList();
    }

    public static List<ArticleSummary> fromArticleInfosAndCreators(List<ArticleInfo> articleInfos, List<Creator> creators) {
        return IntStream.range(0, articleInfos.size())
                .mapToObj(i -> fromArticleInfoAndCreator(articleInfos.get(i), creators.get(i)))
                .toList();
    }

    private static ArticleSummary fromArticleInfoAndCreator(ArticleInfo articleInfo, Creator creator) {
        return new ArticleSummary(
                articleInfo.getId(),
                articleInfo.getTitle(),
                articleInfo.getCreatedAt(),
                creator
        );
    }
}
