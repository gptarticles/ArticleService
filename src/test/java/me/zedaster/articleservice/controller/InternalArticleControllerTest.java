package me.zedaster.articleservice.controller;

import me.zedaster.articleservice.dto.article.ArticleData;
import me.zedaster.articleservice.service.ArticleService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InternalArticleController.class)
public class InternalArticleControllerTest {
    private static final String TEST_TITLE = "Test title".repeat(2);

    private static final String TEST_CONTENT = "Test content".repeat(10);

    /**
     * Mock MVC object for testing.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * Mock article service
     */
    @MockitoBean
    private ArticleService articleService;

    // createArticle POST /protected/articles/user/ - Creates a new article by current user.
    // updateUserArticles PUT /protected/articles/user/ - Updates articles that written by current user

    /**
     * Check if the controller calls create method in the service
     */
    @Test
    public void createArticle() throws Exception {
        when(articleService.createArticle(1, new ArticleData(TEST_TITLE, TEST_CONTENT)))
                .thenReturn(123L);

        String contentJson = """
                {
                  "title": "%s",
                  "content": "%s",
                  "creatorId": 1
                }""".formatted(TEST_TITLE, TEST_CONTENT);

        mockMvc.perform(post("/internal/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content((contentJson)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.articleId").value(123));

        verify(articleService, times(1)).createArticle(anyLong(), any());
    }

    /**
     * Check if the controller calls update method in the service
     */
    @Test
    public void updateArticleException() throws Exception {
        String contentJson = """
                {
                  "title": "%s",
                  "content": "%s"
                }""".formatted(TEST_TITLE, TEST_CONTENT);

        mockMvc.perform(put("/internal/articles/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content((contentJson)))
                .andExpect(status().isOk());

        Mockito.verify(articleService, times(1))
                .updateArticle(2L, new ArticleData(TEST_TITLE, TEST_CONTENT));
    }
}
