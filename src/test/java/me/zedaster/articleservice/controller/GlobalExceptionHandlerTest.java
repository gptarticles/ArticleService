package me.zedaster.articleservice.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import me.zedaster.articleservice.dto.article.ArticleData;
import me.zedaster.articleservice.service.ArticleService;
import me.zedaster.articleservice.service.ArticleServiceException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({ArticleController.class, InternalArticleController.class})
public class GlobalExceptionHandlerTest {

    @MockitoBean
    private ArticleService articleService;

    /**
     * Mock MVC object for testing.
     */
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void handleArticleServiceException() throws Exception {
        when(articleService.createArticle(anyLong(), any(ArticleData.class)))
                .thenThrow(new ArticleServiceException("Some error message"));

        String contentJson = """
                {
                  "title": "a",
                  "content": "b",
                  "creatorId": 1
                }""";

        mockMvc.perform(post("/internal/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content((contentJson)))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.message").value("Some error message"));
    }

    @Test
    public void handleNotFoundException() throws Exception {
        when(articleService.getArticle(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/articles/1"))
                .andExpect(status().is(404))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.message").value("Article not found"));
    }

    @Test
    public void handleConstraintViolationException() throws Exception {
        ConstraintViolation<?> mockViolation1 = mockConstraintViolation("first", "First validation exception");
        ConstraintViolation<?> mockViolation2 = mockConstraintViolation("second", "Second validation exception");

        ConstraintViolationException mockException = mock(ConstraintViolationException.class);
        Set<ConstraintViolation<?>> mockSet = mock(Set.class);
        when(mockSet.iterator()).thenReturn(List.of(mockViolation1, mockViolation2).iterator());
        doCallRealMethod().when(mockSet).forEach(any(Consumer.class));
        when(mockException.getConstraintViolations()).thenReturn(mockSet);
        when(articleService.createArticle(anyLong(), any(ArticleData.class)))
                .thenThrow(mockException);

        String contentJson = """
                {
                  "title": "a",
                  "content": "b",
                  "creatorId": 1
                }""";

        mockMvc.perform(post("/internal/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content((contentJson)))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$.message").value("Error validating the request data!"))
                .andExpect(jsonPath("$.errorsByField.*", hasSize(2)))
                .andExpect(jsonPath("$.errorsByField.first").value("First validation exception"))
                .andExpect(jsonPath("$.errorsByField.second").value("Second validation exception"));
    }


    @NotNull
    private ConstraintViolation<?> mockConstraintViolation(String pathName, String message) {
        ConstraintViolation<?> mockViolation = mock(ConstraintViolation.class);
        when(mockViolation.getMessage()).thenReturn(message);
        Path mockPath = mock(Path.class);
        Path.Node mockNode = mock(Path.Node.class);
        when(mockNode.getName()).thenReturn(pathName);
        when(mockPath.spliterator()).thenReturn(Set.of(mockNode).spliterator());
        when(mockViolation.getPropertyPath()).thenReturn(mockPath);
        return mockViolation;
    }


}
