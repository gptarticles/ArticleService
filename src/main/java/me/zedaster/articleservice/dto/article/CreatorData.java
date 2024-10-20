package me.zedaster.articleservice.dto.article;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Data of creator of the article
 */
@AllArgsConstructor
@Data
public class CreatorData {
    /**
     * Username of the creator
     */
    private String username;
}
