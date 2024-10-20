package me.zedaster.articleservice.dto.request;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.zedaster.articleservice.dto.article.ArticleData;

/**
 * Request that contains payload of the access token and article data
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class ArticleDataRequest {
    /**
     * Payload of the access token
     */
    private final TokenPayload tokenPayload;

    /**
     * Data of the article
     */
    @Valid
    private final ArticleData articleData;
}
