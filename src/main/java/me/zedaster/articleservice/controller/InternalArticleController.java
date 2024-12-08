package me.zedaster.articleservice.controller;

import lombok.AllArgsConstructor;
import me.zedaster.articleservice.dto.article.ArticleData;
import me.zedaster.articleservice.dto.request.CreateArticleRequest;
import me.zedaster.articleservice.dto.response.CreateArticleResponse;
import me.zedaster.articleservice.service.ArticleService;
import me.zedaster.articleservice.service.ArticleServiceException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/articles")
@AllArgsConstructor
public class InternalArticleController {
    /**
     * Service that handles operations with articles
     */
    private final ArticleService articleService;

    /**
     * Creates a new article
     * Returns 200 if everything is okay, otherwise 400 with error message.
     * @param data Body with article data and creator ID
     * @throws IllegalArgumentException If some parameters are incorrect
     * @return Object with ID of the created article
     */
    @PostMapping("")
    public CreateArticleResponse createArticle(@RequestBody CreateArticleRequest data) throws ArticleServiceException {
        long articleId = articleService.createArticle(data.getCreatorId(), data.getArticleData());
        return new CreateArticleResponse(articleId);
    }

    /**
     * Edits an existing article
     * Returns 200 if everything is okay, otherwise 400 with error message.
     * @param articleId ID of the article
     * @param data Body with article data
     * @throws IllegalArgumentException If some parameters are incorrect
     */
    @PutMapping("/{articleId}")
    public void editArticle(@PathVariable long articleId, @RequestBody ArticleData data) throws ArticleServiceException {
        articleService.updateArticle(articleId, data);
    }
}
