package me.zedaster.articleservice.controller;

import lombok.AllArgsConstructor;
import me.zedaster.articleservice.dto.response.CreateArticleResponse;
import me.zedaster.articleservice.dto.request.ArticleDataRequest;
import me.zedaster.articleservice.model.ArticleSummary;
import me.zedaster.articleservice.service.ArticleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for protected article requests (user must authorize before these actions).
 * <br>
 * For protected path api gateway adds userId, username fields to body of POST/PUT and to query params of GET/DELETE
 * requests
 */
@RestController
@RequestMapping("/protected/articles")
@AllArgsConstructor
public class ProtectedArticleController {

    /**
     * Service that handles operations with articles
     */
    private final ArticleService articleService;

    /**
     * Gets articles that written by current user
     * @return List of article summaries
     */
    @GetMapping("/user")
    public List<ArticleSummary> getUserArticles(@RequestParam("tokenPayload[userId]") long userId) {
        return articleService.getArticleSummariesByUserId(userId);
    }

    /**
     * Creates a new article by current user.
     * Returns 200 if everything is okay, otherwise 400 with error message.
     * @param request
     * @throws IllegalArgumentException If some parameters are incorrect
     * @return Object with ID of the created article
     */
    @PostMapping("/user")
    public CreateArticleResponse createArticle(@RequestBody ArticleDataRequest request) throws IllegalArgumentException {
        long articleId = articleService.createArticle(request.getTokenPayload().getUserId(), request.getArticleData());
        return new CreateArticleResponse(articleId);
    }

    /**
     * Edits an existing article by current user.
     * Returns 200 if everything is okay, otherwise 400 with error message.
     * @param articleId ID of the article
     * @param request Body with token payload and article data
     * @throws IllegalArgumentException If some parameters are incorrect
     */
    @PutMapping("/user/{id}")
    public void editArticle(@PathVariable("id") long articleId, @RequestBody ArticleDataRequest request)
            throws IllegalArgumentException {
        articleService.updateArticle(request.getTokenPayload().getUserId(), articleId, request.getArticleData());
    }
}