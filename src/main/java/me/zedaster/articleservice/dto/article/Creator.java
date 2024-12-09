package me.zedaster.articleservice.dto.article;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Data of creator of the article
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode(of = "id")
public class Creator {
    /**
     * User ID of the creator
     */
    private long id;

    /**
     * Username of the creator
     */
    private String name;
}
