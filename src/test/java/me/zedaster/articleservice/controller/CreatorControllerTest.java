package me.zedaster.articleservice.controller;


import me.zedaster.articleservice.dto.request.UpdateCreatorRequest;
import me.zedaster.articleservice.service.CreatorService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class CreatorControllerTest {
    /**
     * Mock MVC object for testing.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * Mock article service
     */
    @MockBean
    private CreatorService creatorService;

    @Test
    public void testChangeUsername() throws Exception {
        String contentJson = """
                {
                  "newUsername": "jolly"
                }""";

        mockMvc.perform(put("/internal/articles/creators/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content((contentJson)))
                .andExpect(status().isOk());

        Mockito.verify(creatorService, times(1))
                .updateCreator(123L, new UpdateCreatorRequest("jolly"));
    }
}
