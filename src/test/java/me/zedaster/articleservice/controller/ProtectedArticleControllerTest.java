package me.zedaster.articleservice.controller;

import me.zedaster.articleservice.dto.article.ArticleData;
import me.zedaster.articleservice.service.ArticleService;
import me.zedaster.articleservice.service.CreatorService;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for {@link ProtectedArticleController}
 */
@WebMvcTest(ProtectedArticleController.class)
public class ProtectedArticleControllerTest {

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
    @MockBean
    private ArticleService articleService;

    /**
     * Mock creator service
     */
    @MockBean
    private CreatorService creatorService;

    // getUserArticles GET - Gets articles that written by current user
    // createArticle POST /protected/articles/user/ - Creates a new article by current user.
    // updateUserArticles PUT /protected/articles/user/ - Updates articles that written by current user

    /**
     * Check if getter of user articles supports first default page
     */
    @Test
    public void getDefaultPageOfUserArticles() throws Exception {
        mockMvc.perform(get("/protected/articles/user?tokenPayload[userId]=1"))
                .andExpect(status().isOk());

        Mockito.verify(articleService, times(1)).getArticleSummariesByUserId(1, 1);
    }

    /**
     * Check if getter of user articles is called
     */
    @Test
    public void getUserArticles() throws Exception {
        mockMvc.perform(get("/protected/articles/user?tokenPayload[userId]=1&page=2"))
                .andExpect(status().isOk());

        Mockito.verify(articleService, times(1)).getArticleSummariesByUserId(1, 2);
    }

    /**
     * Check if the controller calls create method in the service
     */
    @Test
    public void createArticle() throws Exception {
        doNothing().when(creatorService).addIfNotExist(1, "john");
        when(articleService.createArticle(1, new ArticleData(TEST_TITLE, TEST_CONTENT)))
                .thenReturn(123L);

        InOrder inOrder = inOrder(creatorService, articleService);

        String contentJson = """
                {
                  "tokenPayload": {
                    "userId": 1,
                    "username": "john"
                  },
                  "articleData": {
                    "title": "%s",
                    "content": "%s"
                  }
                }""".formatted(TEST_TITLE, TEST_CONTENT);

        mockMvc.perform(post("/protected/articles/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content((contentJson)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.articleId").value(123));

        inOrder.verify(creatorService, times(1)).addIfNotExist(1, "john");
        inOrder.verify(articleService, times(1)).createArticle(anyLong(), any());
    }

    /**
     * Check if the controller calls update method in the service
     */
    @Test
    public void updateArticleException() throws Exception {
        String contentJson = """
                {
                  "tokenPayload": {
                    "userId": 1,
                    "username": "john"
                  },
                  "articleData": {
                    "title": "%s",
                    "content": "%s"
                  }
                }""".formatted(TEST_TITLE, TEST_CONTENT);

        mockMvc.perform(put("/protected/articles/user/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content((contentJson)))
                .andExpect(status().isOk());

        Mockito.verify(articleService, times(1))
                .updateArticle(1L, 2L, new ArticleData(TEST_TITLE, TEST_CONTENT));
    }

}
