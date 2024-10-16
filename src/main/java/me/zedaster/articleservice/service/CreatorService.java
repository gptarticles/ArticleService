package me.zedaster.articleservice.service;

import me.zedaster.articleservice.dto.request.UpdateCreatorRequest;
import org.springframework.stereotype.Service;

/**
 * Service to work with data of the creators
 */
@Service
public class CreatorService {
    /**
     * Update data of a specific creator
     * @param userId ID of the user (creator)
     * @param newData Fields to change for the creator
     */
    public void updateCreator(long userId, UpdateCreatorRequest newData) {
        // TODO
    }
}
