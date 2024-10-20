package me.zedaster.articleservice.service;

import me.zedaster.articleservice.configuration.S3Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

/**
 * Tests for {@link ContentService}.
 */
@SpringBootTest(classes = {ContentService.class, ContentServiceTest.TestConfig.class})
@Testcontainers
public class ContentServiceTest {
    private static final String MINIO_IMAGE = "minio/minio:RELEASE.2024-10-13T13-34-11Z";

    @Container
    private static final MinIOContainer minIoContainer = new MinIOContainer(MINIO_IMAGE);

    @TestConfiguration
    public static class TestConfig {
        @Bean
        @Primary
        public S3Configuration s3Configuration() {
            S3Configuration s3Config = Mockito.mock(S3Configuration.class);
            Mockito.when(s3Config.getAccessKey()).thenReturn(minIoContainer.getUserName());
            Mockito.when(s3Config.getSecretKey()).thenReturn(minIoContainer.getPassword());
            Mockito.when(s3Config.getEndpointUrl()).thenReturn(minIoContainer.getS3URL());
            Mockito.when(s3Config.getContentBucketName()).thenReturn("contents");
            return s3Config;
        }

    }

    @Autowired
    private ContentService contentService;

//    @Autowired
//    private S3Configuration s3Configuration;

    @BeforeAll
    static void beforeAll() {
        minIoContainer.start();

    }

    @AfterAll
    static void afterAll() {
        minIoContainer.stop();
    }

    // Article ID and content are already validated, so we don't need to test them

    /**
     * Tests saving, getting and updating content.
     */
    @Test
    public void testSaveAndGetAndUpdate() throws ContentServiceException {
        // Get non-existent content
        Assertions.assertTrue(contentService.getContentByArticleId(1L).isEmpty());
        // Save and get existent content
        contentService.saveContent(1L, "content");
        Optional<String> content = contentService.getContentByArticleId(1L);
        Assertions.assertTrue(content.isPresent());
        Assertions.assertEquals("content", content.get());
        // Update content to the same value
        contentService.saveContent(1L, "content");
        Assertions.assertEquals(contentService.getContentByArticleId(1L).get(), "content");
        // Update content to a new value
        contentService.saveContent(1L, "new content");
        Assertions.assertEquals(contentService.getContentByArticleId(1L).get(), "new content");
    }

    // TODO: Handle 500 error
}
