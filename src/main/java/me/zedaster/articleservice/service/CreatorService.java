package me.zedaster.articleservice.service;

import me.zedaster.articleservice.dto.request.UpdateCreatorRequest;
import org.springframework.stereotype.Service;

/**
 * Service to work with data of the creators
 */
@Service
public class CreatorService {

    /**
     * Add a creator if he doesn't exist in the database
     * @param userId User ID of the creator
     * @param username Username of the creator
     */
    public void addIfNotExist(long userId, String username) {
        // TODO
    }

    /**
     * Update data of a specific creator
     * @param userId ID of the user (creator)
     * @param newData Fields to change for the creator
     */
    public void updateCreator(long userId, UpdateCreatorRequest newData) {
        // TODO
    }
}
