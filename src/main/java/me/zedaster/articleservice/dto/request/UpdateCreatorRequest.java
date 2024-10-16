package me.zedaster.articleservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request for updating some data of creator
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateCreatorRequest {
    /**
     * New username for the creator
     */
    private String newUsername;
}
