package me.zedaster.articleservice.dto.request;

import lombok.Data;
import me.zedaster.articleservice.dto.article.ArticleData;

@Data
public class CreateArticleRequest {
    /**
     * Title of the article
     */
    private final String title;

    /**
     * Content of the article
     */
    private final String content;

    /**
     * ID of the creator of the article
     */
    private final long creatorId;


    public ArticleData getArticleData() {
        return new ArticleData(title, content);
    }
}
