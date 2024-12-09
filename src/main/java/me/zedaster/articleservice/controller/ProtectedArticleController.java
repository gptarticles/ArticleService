package me.zedaster.articleservice.controller;

import lombok.AllArgsConstructor;
import me.zedaster.articleservice.dto.article.ArticleSummary;
import me.zedaster.articleservice.dto.article.Creator;
import me.zedaster.articleservice.service.ArticleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public List<ArticleSummary> getUserArticles(@RequestParam("tokenPayload.sub") long userId,
                                                @RequestParam("tokenPayload.username") String username,
                                                @RequestParam(value = "page", required = false) Integer pageNumber) {
        if (pageNumber == null) {
            pageNumber = 1;
        }
        Creator creator = new Creator(userId, username);
        return articleService.getArticleSummariesByCreator(creator, pageNumber);
    }
}
