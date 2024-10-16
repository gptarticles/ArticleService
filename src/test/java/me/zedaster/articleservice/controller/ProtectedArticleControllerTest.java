package me.zedaster.articleservice.controller;

import me.zedaster.articleservice.dto.ArticleData;
import me.zedaster.articleservice.service.ArticleService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for {@link ProtectedArticleController}
 */
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class ProtectedArticleControllerTest {
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

    // getUserArticles GET - Gets articles that written by current user
    // createArticle POST /protected/articles/user/ - Creates a new article by current user.
    // updateUserArticles PUT /protected/articles/user/ - Updates articles that written by current user

    /**
     * Check if the controller calls getter of user articles in the service
     */
    @Test
    public void getUserArticles() throws Exception {
        mockMvc.perform(get("/protected/articles/user?tokenPayload[userId]=1"))
                .andExpect(status().isOk());

        Mockito.verify(articleService, times(1)).getArticleSummariesByUserId(1);
    }

    /**
     * Check if the controller calls create method in the service
     */
    @Test
    public void createArticle() throws Exception {
        when(articleService.createArticle(1, new ArticleData("Test title", "Test content")))
                .thenReturn(123L);

        String contentJson = """
                {
                  "tokenPayload": {
                    "userId": 1,
                    "username": "john"
                  },
                  "articleData": {
                    "title": "Test title",
                    "content": "Test content"
                  }
                }""";

        mockMvc.perform(post("/protected/articles/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content((contentJson)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.articleId").value(123));

        Mockito.verify(articleService, times(1)).createArticle(anyLong(), any());
    }

    /**
     * Check if the controller handles an exception from create method in the service
     */
    @Test
    public void createArticleException() throws Exception {
        when(articleService.createArticle(1, new ArticleData("Test title", "Test content")))
                .thenThrow(new IllegalArgumentException("some exception"));

        String contentJson = """
                {
                  "tokenPayload": {
                    "userId": 1,
                    "username": "john"
                  },
                  "articleData": {
                    "title": "Test title",
                    "content": "Test content"
                  }
                }""";

        mockMvc.perform(post("/protected/articles/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content((contentJson)))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.message").value("some exception"));
    }

    /**
     * Check if the controller calls update method in the service, and it handles some exception.
     */
    @Test
    public void updateArticleException() throws Exception {
        Mockito.doThrow(new IllegalArgumentException("test exception"))
                .when(articleService)
                .updateArticle(1, 1, new ArticleData("Test title", "Test content"));

        String contentJson = """
                {
                  "tokenPayload": {
                    "userId": 1,
                    "username": "john"
                  },
                  "articleData": {
                    "title": "Test title",
                    "content": "Test content"
                  }
                }""";

        mockMvc.perform(put("/protected/articles/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content((contentJson)))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.message").value("test exception"));

        Mockito.verify(articleService, times(1))
                .updateArticle(anyLong(), anyLong(), any());
    }

}
