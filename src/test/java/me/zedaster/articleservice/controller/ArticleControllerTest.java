package me.zedaster.articleservice.controller;

import me.zedaster.articleservice.dto.article.Article;
import me.zedaster.articleservice.dto.article.ArticleSummary;
import me.zedaster.articleservice.dto.article.CreatorData;
import me.zedaster.articleservice.entity.ArticleInfo;
import me.zedaster.articleservice.entity.Creator;
import me.zedaster.articleservice.service.ArticleService;
import me.zedaster.articleservice.util.TestUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for {@link ArticleController}
 */
@WebMvcTest(ArticleController.class)
public class ArticleControllerTest {
    /**
     * Mock MVC object for testing.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * Mock article service
     */
    @MockBean
    private ArticleService articleService;

    /**
     * Checks if getter of a certain article is called
     * @throws Exception If something in the mock request went wrong
     */
    @Test
    public void getCertainArticle() throws Exception {
        Creator fakeCreator = new Creator(123L, "john");
        Instant createdAt = TestUtils.createInstantOf(2021, 1, 1, 12, 30, 0);
        ArticleInfo fakeInfo = new ArticleInfo("a".repeat(15), createdAt, fakeCreator);
        fakeInfo.setId(321L);
        Article fakeArticle = new Article(fakeInfo, "a".repeat(100));

        when(articleService.getArticle(321L)).thenReturn(Optional.of(fakeArticle));

        mockMvc.perform(get("/articles/321"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(5)))
                .andExpect(jsonPath("$.id").value(321L))
                .andExpect(jsonPath("$.title").value("a".repeat(15)))
                .andExpect(jsonPath("$.content").value("a".repeat(100)))
                .andExpect(jsonPath("$.createdAt").value("2021-01-01T12:30:00Z"))
                .andExpect(jsonPath("$.creatorData.*", hasSize(1)))
                .andExpect(jsonPath("$.creatorData.username").value("john"));
    }

    /**
     * Checks if getter of recent articles is called
     * @throws Exception If something in the mock request went wrong
     */
    @Test
    public void getRecentArticles() throws Exception {
        Instant createdAt1 = TestUtils.createInstantOf(2021, 1, 1, 12, 30, 0);
        Instant createdAt2 = TestUtils.createInstantOf(2022, 1, 1, 12, 30, 0);

        List<ArticleSummary> articleSummaries = List.of(
                new ArticleSummary(1L, "a".repeat(15), createdAt1, new CreatorData("john")),
                new ArticleSummary(2L, "b".repeat(15), createdAt2, new CreatorData("bill"))
        );
        when(articleService.getRecentArticleSummaries(123)).thenReturn(articleSummaries);

        mockMvc.perform(get("/articles/recent?page=123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("a".repeat(15)))
                .andExpect(jsonPath("$[0].createdAt").value("2021-01-01T12:30:00Z"))
                .andExpect(jsonPath("$[0].creatorData.*", hasSize(1)))
                .andExpect(jsonPath("$[0].creatorData.username").value("john"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].title").value("b".repeat(15)))
                .andExpect(jsonPath("$[1].createdAt").value("2022-01-01T12:30:00Z"))
                .andExpect(jsonPath("$[1].creatorData.*", hasSize(1)))
                .andExpect(jsonPath("$[1].creatorData.username").value("bill"));
    }

    /**
     * Checks if getter of recent articles support default page
     * @throws Exception If something in the mock request went wrong
     */
    @Test
    public void defaultPageOfRecentArticles() throws Exception {
        when(articleService.getRecentArticleSummaries(1)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/articles/recent"))
                .andExpect(status().isOk());

        Mockito.verify(articleService, times(1)).getRecentArticleSummaries(1);
    }
}
