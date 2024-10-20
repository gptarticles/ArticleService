package me.zedaster.articleservice.controller;

import lombok.AllArgsConstructor;
import me.zedaster.articleservice.dto.request.UpdateCreatorRequest;
import me.zedaster.articleservice.service.CreatorService;
import me.zedaster.articleservice.service.CreatorServiceException;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for internal requests to work with creators of the articles
 */
@AllArgsConstructor
@RestController
@RequestMapping("/internal/articles/creators")
public class CreatorController {
    /**
     * Service to work with creators
     */
    private final CreatorService creatorService;

    /**
     * Change data of saved creator
     */
    @PutMapping("/{id}")
    public void changeData(@PathVariable("id") long userId, @RequestBody UpdateCreatorRequest request) throws CreatorServiceException {
        creatorService.updateCreator(userId, request.getNewUsername());
    }
}
