package me.zedaster.articleservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Payload of the access token
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class TokenPayload {
    /**
     * ID of user that is authorized
     */
    private final long userId;

    /**
     * Username of user that is authorized
     */
    private final String username;

}
