package me.zedaster.articleservice.service;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import me.zedaster.articleservice.entity.Creator;
import me.zedaster.articleservice.repository.CreatorRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * Service to work with data of the creators
 */
@Service
@Validated
@AllArgsConstructor
public class CreatorService {

    private static final String INCORRECT_USER_ID = "User id must be bigger than zero!";

    private static final String NULL_USER_NAME = "Username must be not null!";

    private static final String INCORRECT_USER_NAME = "Username does not meet the requirements!";

    private static final String USER_NAME_REGEX = "^[a-zA-Z][a-zA-Z0-9._]{2,31}$";


    private final CreatorRepository creatorRepository;

    /**
     * Add a creator if he doesn't exist in the database
     * @param userId User ID of the creator
     * @param username Username of the creator
     */
    public void addIfNotExist(@Min(value = 1, message = INCORRECT_USER_ID) long userId,
                              @NotNull(message = NULL_USER_NAME)
                              @Pattern(regexp = USER_NAME_REGEX, message = INCORRECT_USER_NAME) String username) {
        if (!creatorRepository.existsById(userId)) {
            creatorRepository.save(new Creator(userId, username));
        }
    }

    /**
     * Update data of a specific creator
     * @param userId ID of the user (creator)
     * @param newUsername Username of the creator
     */
    public void updateCreator(@Min(value = 1, message = INCORRECT_USER_ID) long userId,
                              @NotNull(message = NULL_USER_NAME)
                              @Pattern(regexp = USER_NAME_REGEX, message = INCORRECT_USER_NAME) String newUsername)
            throws CreatorServiceException {
        if (!creatorRepository.existsById(userId)) {
            throw new CreatorServiceException("Creator with the ID doesn't exist!");
        }
        creatorRepository.save(new Creator(userId, newUsername));
    }
}
