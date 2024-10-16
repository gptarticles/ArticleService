package me.zedaster.articleservice.controller;

import me.zedaster.articleservice.service.ArticleService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for {@link ArticleController}
 */
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
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
     * Checks if getter of a certain article is called and it handles NumberFormatException
     * @throws Exception If something in the mock request went wrong
     */
    @Test
    public void wrongNumberInGetArticle() throws Exception {
        when(articleService.getArticle(-1)).thenThrow(new NumberFormatException("test msg"));

        mockMvc.perform(get("/articles/-1"))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.message").value("test msg"));

       Mockito.verify(articleService, times(1)).getArticle(anyLong());
    }

    /**
     * Checks if getter of recent articles is called and it handles NumberFormatException
     * @throws Exception If something in the mock request went wrong
     */
    @Test
    public void wrongNumberInGetRecentArticle() throws Exception {
        when(articleService.getRecentArticleSummaries(-1)).thenThrow(new NumberFormatException("test msg"));

        mockMvc.perform(get("/articles/recent?page=-1"))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.message").value("test msg"));
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
