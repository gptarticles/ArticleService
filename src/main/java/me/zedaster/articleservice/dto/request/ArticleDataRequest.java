package me.zedaster.articleservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.zedaster.articleservice.dto.ArticleData;
import me.zedaster.articleservice.dto.TokenPayload;

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
    private final ArticleData articleData;
}
