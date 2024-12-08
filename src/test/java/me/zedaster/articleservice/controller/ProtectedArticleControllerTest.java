package me.zedaster.articleservice.controller;

import me.zedaster.articleservice.service.ArticleService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for {@link ProtectedArticleController}
 */
@WebMvcTest(ProtectedArticleController.class)
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

    /**
     * Check if getter of user articles supports first default page
     */
    @Test
    public void getDefaultPageOfUserArticles() throws Exception {
        mockMvc.perform(get("/protected/articles/user?tokenPayload.userId=1"))
                .andExpect(status().isOk());

        Mockito.verify(articleService, times(1)).getArticleSummariesByUserId(1, 1);
    }

    /**
     * Check if getter of user articles is called
     */
    @Test
    public void getUserArticles() throws Exception {
        mockMvc.perform(get("/protected/articles/user?tokenPayload.userId=1&page=2"))
                .andExpect(status().isOk());

        Mockito.verify(articleService, times(1)).getArticleSummariesByUserId(1, 2);
    }

}
