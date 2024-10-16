package me.zedaster.articleservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * DTO that represents an error message.
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode
public class ErrorDto {
    /**
     * Error message.
     */
    private final String message;
}
