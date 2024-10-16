package me.zedaster.articleservice.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Data of article for creating or updating
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class ArticleData {
    /**
     * Title of the article
     */
    private final String title;

    /**
     * Content of the article
     */
    private final String content;
}
