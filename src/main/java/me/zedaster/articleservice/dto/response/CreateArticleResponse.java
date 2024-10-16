package me.zedaster.articleservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * DTO for response after article creation
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class CreateArticleResponse {
    /**
     * ID of the new article
     */
    private final long articleId;
}
