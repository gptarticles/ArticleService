package me.zedaster.articleservice.controller;

import lombok.AllArgsConstructor;
import me.zedaster.articleservice.dto.article.Article;
import me.zedaster.articleservice.dto.article.ArticleSummary;
import me.zedaster.articleservice.service.ArticleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for public article requests
 */
@RestController
@RequestMapping("/articles")
@AllArgsConstructor
public class ArticleController {
    /**
     * Service that handles operations with articles
     */
    private final ArticleService articleService;

    /**
     * Gets certain article by its ID
     * @param id ID of the article
     * @return Article object
     */
    @GetMapping("/{id}")
    public Article getArticle(@PathVariable long id) throws NotFoundException {
        return articleService.getArticle(id).orElseThrow(() -> new NotFoundException("Article not found"));
    }

    /**
     * Get articles that has been published recently
     * @param pageNumber Number of page. Default value is 1
     * @return list of summaries of recent articles
     */
    @GetMapping("/recent")
    public List<ArticleSummary> getRecentArticles(@RequestParam(value = "page", required = false) Integer pageNumber)
            throws NumberFormatException {
        if (pageNumber == null) {
            pageNumber = 1;
        }
        return articleService.getRecentArticleSummaries(pageNumber);
    }
}
