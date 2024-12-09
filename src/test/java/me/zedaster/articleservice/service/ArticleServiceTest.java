package me.zedaster.articleservice.service;

import jakarta.validation.ConstraintViolationException;
import me.zedaster.articleservice.dto.article.Article;
import me.zedaster.articleservice.dto.article.ArticleData;
import me.zedaster.articleservice.dto.article.ArticleSummary;
import me.zedaster.articleservice.dto.article.Creator;
import me.zedaster.articleservice.entity.ArticleInfo;
import me.zedaster.articleservice.repository.ArticleInfoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static me.zedaster.articleservice.util.TestUtils.createInstantOf;
import static org.mockito.ArgumentMatchers.*;

/**
 * Tests for {@link ArticleService}
 */
@SpringBootTest(classes = {ArticleService.class, ValidationAutoConfiguration.class})
public class ArticleServiceTest {
    @Autowired
    private ArticleService articleService;

    @MockitoBean
    private ContentService contentService;

    @MockitoBean
    private CreatorService creatorService;

    @MockitoBean
    private ArticleInfoRepository articleInfoRepository;

    public ArticleServiceTest(ApplicationContext applicationContext) {

    }

    /**
     * Checks if the getter of an article is called and the result is correct
     */
    @Test
    public void getExistingArticle() throws ContentServiceException {
        Instant fakeCreatedAt = createInstantOf(2024, 1, 1, 17, 40, 0);
        ArticleInfo fakeArticleInfo = new ArticleInfo("Test title", fakeCreatedAt, 123L);
        fakeArticleInfo.setId(1L);

        Mockito.when(articleInfoRepository.findById(1L)).thenReturn(Optional.of(fakeArticleInfo));
        Mockito.when(contentService.getContentByArticleId(1L)).thenReturn(Optional.of("Test content"));
        Mockito.when(creatorService.getCreator(123L)).thenReturn(new Creator(123L, "john"));

        Optional<Article> optionalArticle = articleService.getArticle(1);
        Assertions.assertTrue(optionalArticle.isPresent());
        Article article = optionalArticle.get();

        Assertions.assertEquals(1L, article.getId());
        Assertions.assertEquals("Test title", article.getTitle());
        Assertions.assertEquals("Test content", article.getContent());
        Assertions.assertEquals(fakeCreatedAt, article.getCreatedAt());
        Assertions.assertEquals(123L, article.getCreator().getId());
        Assertions.assertEquals("john", article.getCreator().getName());
    }

    /**
     * Checks if the getter of an article with an incorrect ID throws an exception
     */
    @Test
    public void getArticleWithIncorrectId() {
        ConstraintViolationException ex = Assertions.assertThrows(ConstraintViolationException.class,
                () -> articleService.getArticle(0));
        Assertions.assertEquals(1, ex.getConstraintViolations().size());
        Assertions.assertEquals("Article id must be bigger than zero!",
                ex.getConstraintViolations().stream().findFirst().get().getMessage());
    }

    /**
     * Checks if the getter of a non-existing article returns an empty optional
     */
    @Test
    public void getNonExistingArticle() throws ContentServiceException {
        Mockito.when(articleInfoRepository.findById(123L)).thenReturn(Optional.empty());
        Mockito.when(contentService.getContentByArticleId(123L)).thenReturn(Optional.empty());

        Optional<Article> article = articleService.getArticle(123);
        Assertions.assertTrue(article.isEmpty());
    }

    @Test
    public void getArticleWithContentServiceException() throws ContentServiceException {
        Instant fakeCreatedAt = createInstantOf(2024, 1, 1, 17, 40, 0);
        ArticleInfo fakeArticleInfo = new ArticleInfo("Test title", fakeCreatedAt, 123L);
        fakeArticleInfo.setId(1L);

        Mockito.when(articleInfoRepository.findById(1L)).thenReturn(Optional.of(fakeArticleInfo));
        Mockito.when(creatorService.getCreator(123L)).thenReturn(new Creator(123L, "john"));
        Exception cause = new Exception("Test cause");
        Exception contentException = new ContentServiceException("Test exception", cause);
        Mockito.when(contentService.getContentByArticleId(1L)).thenThrow(contentException);

        InternalServerException ex = Assertions.assertThrows(InternalServerException.class, () -> articleService.getArticle(1));
        Assertions.assertEquals("Can't get content of the article!", ex.getMessage());
        Assertions.assertSame(contentException, ex.getCause());
    }

    @Test
    public void getArticleWithNonExistentContent() throws ContentServiceException {
        Instant fakeCreatedAt = createInstantOf(2024, 1, 1, 17, 40, 0);
        ArticleInfo fakeArticleInfo = new ArticleInfo("Test title", fakeCreatedAt, 123L);
        fakeArticleInfo.setId(1L);

        Mockito.when(articleInfoRepository.findById(1L)).thenReturn(Optional.of(fakeArticleInfo));
        Mockito.when(creatorService.getCreator(123L)).thenReturn(new Creator(123L, "john"));
        Mockito.when(contentService.getContentByArticleId(1L)).thenReturn(Optional.empty());

        InternalServerException ex = Assertions.assertThrows(InternalServerException.class, () -> articleService.getArticle(1));
        Assertions.assertEquals("ArticleInfo exists, but there's no content for the article!", ex.getMessage());
    }

    /**
     * Checks if the getter of recent articles is called and the result is correct
     */
    @Test
    public void getSomeRecentArticlesSummaries() {
        // There is only one test because the repository is responsible for pagination
        Instant firstFakeCreatedAt = createInstantOf(2024, 1, 1, 17, 40, 0);
        ArticleInfo firstFakeArticleInfo = new ArticleInfo("Test title one", firstFakeCreatedAt, 123L);
        firstFakeArticleInfo.setId(1L);

        Instant secondFakeCreatedAt = createInstantOf(2024, 1, 2, 17, 40, 0);
        ArticleInfo secondFakeArticleInfo = new ArticleInfo("Test title two", secondFakeCreatedAt, 321L);
        secondFakeArticleInfo.setId(2L);

        List<ArticleInfo> someList = List.of(firstFakeArticleInfo, secondFakeArticleInfo);

        Mockito.when(articleInfoRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, 20)))
                .thenReturn(someList);

        Mockito.when(creatorService.getCreatorsByIds(List.of(123L, 321L)))
                .thenReturn(List.of(new Creator(123L, "john"), new Creator(321L, "billy")));

        List<ArticleSummary> firstPageArticles = articleService.getRecentArticleSummaries(1);

        Assertions.assertEquals(2, firstPageArticles.size());

        Assertions.assertEquals(1L, firstPageArticles.get(0).getId());
        Assertions.assertEquals("Test title one", firstPageArticles.get(0).getTitle());
        Assertions.assertEquals(firstFakeCreatedAt, firstPageArticles.get(0).getCreatedAt());
        Assertions.assertEquals(123L, firstPageArticles.get(0).getCreator().getId());
        Assertions.assertEquals("john", firstPageArticles.get(0).getCreator().getName());

        Assertions.assertEquals(2L, firstPageArticles.get(1).getId());
        Assertions.assertEquals("Test title two", firstPageArticles.get(1).getTitle());
        Assertions.assertEquals(secondFakeCreatedAt, firstPageArticles.get(1).getCreatedAt());
        Assertions.assertEquals(321L, firstPageArticles.get(1).getCreator().getId());
        Assertions.assertEquals("billy", firstPageArticles.get(1).getCreator().getName());
    }

    /**
     * Checks if the getter of recent articles returns empty list if there are no articles
     */
    @Test
    public void getZeroRecentArticlesSummaries() {
        Mockito.when(articleInfoRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(4, 20)))
                .thenReturn(List.of());
        List<ArticleSummary> articles = articleService.getRecentArticleSummaries(5);
        Assertions.assertTrue(articles.isEmpty());
    }

    /**
     * Checks if the getter of recent articles with an incorrect page throws an exception
     */
    @Test
    public void getRecentArticlesSummariesWithIncorrectPage() {
        ConstraintViolationException ex = Assertions.assertThrows(ConstraintViolationException.class,
                () -> articleService.getRecentArticleSummaries(0));
        Assertions.assertEquals(1, ex.getConstraintViolations().size());
        Assertions.assertEquals("Page number must be bigger than zero!",
                ex.getConstraintViolations().stream().findFirst().get().getMessage());
    }

    /**
     * Checks if the getter of articles of a certain user is called and the result is correct
     */
    @Test
    public void getSomeUserArticlesSummariesWithExistingPage() {
        Instant firstFakeCreatedAt = createInstantOf(2024, 1, 1, 17, 40, 0);
        ArticleInfo firstFakeArticleInfo = new ArticleInfo("Test title one", firstFakeCreatedAt, 123L);
        firstFakeArticleInfo.setId(1L);

        Instant secondFakeCreatedAt = createInstantOf(2024, 1, 2, 17, 40, 0);
        ArticleInfo secondFakeArticleInfo = new ArticleInfo("Test title two", secondFakeCreatedAt, 123L);
        secondFakeArticleInfo.setId(2L);

        List<ArticleInfo> fakeList = List.of(firstFakeArticleInfo, secondFakeArticleInfo);
        Mockito.when(articleInfoRepository.findAllByCreatorIdOrderByCreatedAtDesc(123L, PageRequest.of(0, 20)))
                .thenReturn(fakeList);

        Creator creator = new Creator(123L, "john");
        List<ArticleSummary> firstPageArticles = articleService.getArticleSummariesByCreator(creator, 1);

        Assertions.assertEquals(2, firstPageArticles.size());

        Assertions.assertEquals(1L, firstPageArticles.get(0).getId());
        Assertions.assertEquals("Test title one", firstPageArticles.get(0).getTitle());
        Assertions.assertEquals(firstFakeCreatedAt, firstPageArticles.get(0).getCreatedAt());
        Assertions.assertEquals(123L, firstPageArticles.get(0).getCreator().getId());
        Assertions.assertEquals("john", firstPageArticles.get(0).getCreator().getName());

        Assertions.assertEquals(2L, firstPageArticles.get(1).getId());
        Assertions.assertEquals("Test title two", firstPageArticles.get(1).getTitle());
        Assertions.assertEquals(secondFakeCreatedAt, firstPageArticles.get(1).getCreatedAt());
        Assertions.assertEquals(123L, firstPageArticles.get(1).getCreator().getId());
        Assertions.assertEquals("john", firstPageArticles.get(1).getCreator().getName());
    }

    /**
     * Checks if the getter of articles of a certain user returns empty list if there are no articles
     */
    @Test
    public void getZeroUserArticlesSummariesWithExistingPage() {
        Creator creator = new Creator(123L, "john");
        Mockito.when(articleInfoRepository.findAllByCreatorIdOrderByCreatedAtDesc(123L, PageRequest.of(4, 20)))
                .thenReturn(List.of());

        List<ArticleSummary> articles = articleService.getArticleSummariesByCreator(creator, 5);
        Assertions.assertEquals(0, articles.size());
    }

    /**
     * Checks if the getter of articles of a certain user with an incorrect page throws an exception
     */
    @Test
    public void getUserArticlesSummariesWithIncorrectPage() {
        Creator creator = new Creator(123L, "john");
        ConstraintViolationException ex = Assertions.assertThrows(ConstraintViolationException.class,
                () -> articleService.getArticleSummariesByCreator(creator, 0));
        Assertions.assertEquals(1, ex.getConstraintViolations().size());
        Assertions.assertEquals("Page number must be bigger than zero!",
                ex.getConstraintViolations().stream().findFirst().get().getMessage());
    }

    /**
     * Checks the creation of an article with correct data
     * @throws ArticleServiceException This exception should be not thrown
     */
    @Test
    public void createArticleCorrectly() throws ArticleServiceException, ContentServiceException {
        Creator fakeCreator = new Creator(1L, "john");
        List<String> correctTitles = List.of("a".repeat(15), "a".repeat(100));
        List<String> correctContents = List.of("a".repeat(100), "a".repeat(18_000));
        Instant fakeNow = createInstantOf(2024, 1, 1, 17, 40, 0);

        Mockito.when(creatorService.getCreator(1L)).thenReturn(fakeCreator);

        try (MockedStatic<Instant> mockedInstant = Mockito.mockStatic(Instant.class)) {
            mockedInstant.when(Instant::now).thenReturn(fakeNow);
            for (String title : correctTitles) {
                for (String content : correctContents) {
                    Mockito.when(articleInfoRepository.existsByCreatorIdAndTitle(1L, title))
                            .thenReturn(false);

                    ArticleData fakeData = new ArticleData(title, content);
                    ArticleInfo fakeInfo = new ArticleInfo(title, fakeNow, 1L);
                    ArticleInfo resultInfo = new ArticleInfo(title, fakeNow, 1L);
                    resultInfo.setId(1L);

                    Mockito.when(articleInfoRepository.save(any(ArticleInfo.class)))
                            .thenReturn(resultInfo);

                    long articleId = articleService.createArticle(1L, fakeData);
                    Assertions.assertEquals(1L, articleId);

                    Mockito.verify(articleInfoRepository, Mockito.times(1))
                            .save(argThat(info -> assertArticleInfosEqual(fakeInfo, info)));
                    Mockito.verify(contentService, Mockito.times(1)).saveContent(1L, content);

                    Mockito.reset(articleInfoRepository);
                    Mockito.reset(contentService);
                }
            }
        }

    }

    /**
     * Checks if the creation of an article with incorrect user ID throws an exception
     */
    @Test
    public void createArticleWithIncorrectUserId() {
        String title = "a".repeat(50);
        String content = "a".repeat(5_000);
        ArticleData fakeData = new ArticleData(title, content);

        for (int id : List.of(0, -1)) {
            ConstraintViolationException ex = Assertions.assertThrows(ConstraintViolationException.class,
                    () -> articleService.createArticle(id, fakeData));
            Assertions.assertEquals(1, ex.getConstraintViolations().size());
            Assertions.assertEquals("User id must be bigger than zero!",
                    ex.getConstraintViolations().stream().findFirst().get().getMessage());
        }
    }

    /**
     * Checks if the creation of an article with null data throws an exception
     */
    @Test
    public void createArticleWithNullArticleData() {
        ConstraintViolationException ex = Assertions.assertThrows(ConstraintViolationException.class,
                () -> articleService.createArticle(1, null));
        Assertions.assertEquals(1, ex.getConstraintViolations().size());
        Assertions.assertEquals("Article data must be not null!",
                ex.getConstraintViolations().stream().findFirst().get().getMessage());
    }

    /**
     * Checks if the creation of an article with too long title throws an exception
     */
    @Test
    public void createArticleWithTooLongTitle() {
        String title = "a".repeat(101);
        String content = "a".repeat(5_000);
        ArticleData fakeData = new ArticleData(title, content);

        ConstraintViolationException ex = Assertions.assertThrows(ConstraintViolationException.class,
                () -> articleService.createArticle(1, fakeData));
        Assertions.assertEquals(1, ex.getConstraintViolations().size());
        Assertions.assertEquals("Title mustn't contain more than 100 characters!",
                ex.getConstraintViolations().stream().findFirst().get().getMessage());
    }

    /**
     * Checks if the creation of an article with too short title throws an exception
     */
    @Test
    public void createArticleWithTooShortTitle() {
        String title = "a".repeat(14);
        String content = "a".repeat(5_000);
        ArticleData fakeData = new ArticleData(title, content);

        ConstraintViolationException ex = Assertions.assertThrows(ConstraintViolationException.class,
                () -> articleService.createArticle(1, fakeData));
        Assertions.assertEquals(1, ex.getConstraintViolations().size());
        Assertions.assertEquals("Title must contain at least 15 characters!",
                ex.getConstraintViolations().stream().findFirst().get().getMessage());
    }

    /**
     * Checks if the creation of an article with too long content throws an exception
     */
    @Test
    public void createArticleWithTooLongContent() {
        String title = "a".repeat(50);
        String content = "a".repeat(18_001);
        ArticleData fakeData = new ArticleData(title, content);

        ConstraintViolationException ex = Assertions.assertThrows(ConstraintViolationException.class,
                () -> articleService.createArticle(1, fakeData));
        Assertions.assertEquals(1, ex.getConstraintViolations().size());
        Assertions.assertEquals("Content mustn't contain more than 18 000 characters!",
                ex.getConstraintViolations().stream().findFirst().get().getMessage());
    }

    /**
     * Checks if the creation of an article with too short content throws an exception
     */
    @Test
    public void createArticleWithTooShortContent() {
        String title = "a".repeat(50);
        String content = "a".repeat(99);
        ArticleData fakeData = new ArticleData(title, content);

        ConstraintViolationException ex = Assertions.assertThrows(ConstraintViolationException.class,
                () -> articleService.createArticle(1, fakeData));
        Assertions.assertEquals(1, ex.getConstraintViolations().size());
        Assertions.assertEquals("Content must contain at least 100 characters!",
                ex.getConstraintViolations().stream().findFirst().get().getMessage());
    }

    /**
     * Checks if the creation of an article with the same user and title throws an exception
     */
    @Test
    public void createArticleWithTheSameUserAndTitle() {
        Creator fakeCreator = new Creator(1L, "john");
        String title = "a".repeat(50);
        String content = "a".repeat(5_000);
        ArticleData fakeData = new ArticleData(title, content);

        Mockito.when(creatorService.getCreator(1L)).thenReturn(fakeCreator);
        Mockito.when(articleInfoRepository.existsByCreatorIdAndTitle(1L, title))
                .thenReturn(true);

        ArticleServiceException ex = Assertions.assertThrows(ArticleServiceException.class,
                () -> articleService.createArticle(1L, fakeData));
        Assertions.assertEquals("User already has an article with the same title!", ex.getMessage());
    }

    /**
     * Checks updating of an article with correct data
     * @throws ArticleServiceException This exception should be not thrown
     */
    @Test
    public void updateArticleCorrectly() throws ArticleServiceException, ContentServiceException {
        Instant fakeCreatedAt = createInstantOf(2024, 1, 1, 17, 40, 0);
        ArticleInfo fakeArticleInfo = new ArticleInfo("Test title", fakeCreatedAt, 1L);
        fakeArticleInfo.setId(1L);

        Mockito.when(articleInfoRepository.findById(1L)).thenReturn(Optional.of(fakeArticleInfo));

        String newTitle = "b".repeat(50);
        String newContent = "b".repeat(5_000);
        ArticleData fakeData = new ArticleData(newTitle, newContent);

        articleService.updateArticle(1L, fakeData);

        ArticleInfo expectedInfo = new ArticleInfo(newTitle, fakeCreatedAt, 1L);
        expectedInfo.setId(1L);

        Mockito.verify(articleInfoRepository, Mockito.times(1))
                .save(argThat(info -> assertArticleInfosEqual(expectedInfo, info)));
        Mockito.verify(contentService, Mockito.times(1)).saveContent(1L, newContent);
    }

    /**
     * Checks if updating of an article with incorrect article ID throws an exception
     */
    @Test
    public void updateArticleWithIncorrectArticleId() {
        String title = "a".repeat(50);
        String content = "a".repeat(5_000);
        ArticleData fakeData = new ArticleData(title, content);

        for (int id : List.of(0, -1)) {
            ConstraintViolationException ex = Assertions.assertThrows(ConstraintViolationException.class,
                    () -> articleService.updateArticle(id, fakeData));
            Assertions.assertEquals(1, ex.getConstraintViolations().size());
            Assertions.assertEquals("Article id must be bigger than zero!",
                    ex.getConstraintViolations().stream().findFirst().get().getMessage());
        }
    }

    /**
     * Checks if updating of an article with null data throws an exception
     */
    @Test
    public void updateArticleWithNullArticleData() {
        ConstraintViolationException ex = Assertions.assertThrows(ConstraintViolationException.class,
                () -> articleService.updateArticle(1, null));
        Assertions.assertEquals(1, ex.getConstraintViolations().size());
        Assertions.assertEquals("Article data must be not null!",
                ex.getConstraintViolations().stream().findFirst().get().getMessage());
    }

    /**
     * Checks if updating of an article with too long title throws an exception
     */
    @Test
    public void updateArticleWithTooLongTitle() {
        String title = "a".repeat(101);
        String content = "a".repeat(5_000);
        ArticleData fakeData = new ArticleData(title, content);

        ConstraintViolationException ex = Assertions.assertThrows(ConstraintViolationException.class,
                () -> articleService.updateArticle(1, fakeData));
        Assertions.assertEquals(1, ex.getConstraintViolations().size());
        Assertions.assertEquals("Title mustn't contain more than 100 characters!",
                ex.getConstraintViolations().stream().findFirst().get().getMessage());
    }

    /**
     * Checks if updating of an article with too short title throws an exception
     */
    @Test
    public void updateArticleWithTooShortTitle() {
        String title = "a".repeat(14);
        String content = "a".repeat(5_000);
        ArticleData fakeData = new ArticleData(title, content);

        ConstraintViolationException ex = Assertions.assertThrows(ConstraintViolationException.class,
                () -> articleService.updateArticle(1, fakeData));
        Assertions.assertEquals(1, ex.getConstraintViolations().size());
        Assertions.assertEquals("Title must contain at least 15 characters!",
                ex.getConstraintViolations().stream().findFirst().get().getMessage());
    }

    /**
     * Checks if updating of an article with too long content throws an exception
     */
    @Test
    public void updateArticleWithTooLongContent() {
        String title = "a".repeat(50);
        String content = "a".repeat(18_001);
        ArticleData fakeData = new ArticleData(title, content);

        ConstraintViolationException ex = Assertions.assertThrows(ConstraintViolationException.class,
                () -> articleService.updateArticle(1, fakeData));
        Assertions.assertEquals(1, ex.getConstraintViolations().size());
        Assertions.assertEquals("Content mustn't contain more than 18 000 characters!",
                ex.getConstraintViolations().stream().findFirst().get().getMessage());
    }

    /**
     * Checks if updating of an article with too short content throws an exception
     */
    @Test
    public void updateArticleWithTooShortContent() {
        String title = "a".repeat(50);
        String content = "a".repeat(99);
        ArticleData fakeData = new ArticleData(title, content);

        ConstraintViolationException ex = Assertions.assertThrows(ConstraintViolationException.class,
                () -> articleService.updateArticle(1, fakeData));
        Assertions.assertEquals(1, ex.getConstraintViolations().size());
        Assertions.assertEquals("Content must contain at least 100 characters!",
                ex.getConstraintViolations().stream().findFirst().get().getMessage());
    }

    /**
     * Checks if updating of a non-existing article throws an exception
     */
    @Test
    public void updateNonExistingArticle() {
        String title = "a".repeat(50);
        String content = "a".repeat(5_000);
        ArticleData fakeData = new ArticleData(title, content);

        Mockito.when(articleInfoRepository.findById(1L)).thenReturn(Optional.empty());

        ArticleServiceException ex = Assertions.assertThrows(ArticleServiceException.class,
                () -> articleService.updateArticle(1L, fakeData));
        Assertions.assertEquals("Article with the specified ID doesn't exist!", ex.getMessage());
    }

    /**
     * Checks that updating an article to the current title of the same article belonging to the same user
     * works fine
     */
    @Test
    public void updateSameArticleToOldTitle() {
        String title = "a".repeat(50);
        Instant createdAt = createInstantOf(2024, 1, 1, 17, 40, 0);
        ArticleInfo fakeArticleInfo = new ArticleInfo(title, createdAt, 777L);
        fakeArticleInfo.setId(1L);

        Mockito.when(articleInfoRepository.findById(1L)).thenReturn(Optional.of(fakeArticleInfo));
        Mockito.when(articleInfoRepository.findAllByCreatorIdAndTitle(777L, title))
                .thenReturn(List.of(fakeArticleInfo));

        String newContent = "b".repeat(5_000);
        ArticleData fakeData = new ArticleData(title, newContent);
        articleService.updateArticle(1L, fakeData);

        Mockito.verify(articleInfoRepository, Mockito.times(1))
                .save(argThat(info -> assertArticleInfosEqual(fakeArticleInfo, info)));
        Mockito.verify(contentService, Mockito.times(1)).saveContent(1L, newContent);
    }

    /**
     * Checks that updating an article to the current title of another article of the same user is not allowed.
     */
    @Test
    public void updateArticleToTitleOfAnotherArticle() {
        String title = "a".repeat(50);
        Instant createdAt = createInstantOf(2024, 1, 1, 17, 40, 0);
        ArticleInfo fakeArticleInfo = new ArticleInfo(title, createdAt, 777L);
        fakeArticleInfo.setId(1L);

        Mockito.when(articleInfoRepository.findById(1L)).thenReturn(Optional.of(fakeArticleInfo));

        String anotherTitle = "b".repeat(50);
        Instant anotherCreatedAt = createInstantOf(2023, 1, 1, 17, 40, 0);
        ArticleInfo anotherArticleInfo = new ArticleInfo(anotherTitle, anotherCreatedAt, 777L);
        anotherArticleInfo.setId(2L);
        Mockito.when(articleInfoRepository.findAllByCreatorIdAndTitle(777L, anotherTitle))
                .thenReturn(List.of(anotherArticleInfo));

        String someContent = "b".repeat(5_000);
        ArticleData updateData = new ArticleData(anotherTitle, someContent);
        ArticleServiceException ex = Assertions.assertThrows(ArticleServiceException.class, () ->
                articleService.updateArticle(1L, updateData));

        Assertions.assertEquals("User already has another article with the same title!", ex.getMessage());
        Mockito.verify(articleInfoRepository, Mockito.never()).save(any());
        Mockito.verify(contentService, Mockito.never()).saveContent(anyLong(), anyString());
    }

    /**
     * Asserts that two {@link ArticleInfo} objects are equal
     */
    private boolean assertArticleInfosEqual(ArticleInfo expected, ArticleInfo actual) {
        return Objects.equals(expected.getId(), actual.getId()) &&
               Objects.equals(expected.getTitle(), actual.getTitle()) &&
               Objects.equals(expected.getCreatedAt(), actual.getCreatedAt()) &&
               Objects.equals(expected.getCreatorId(), actual.getCreatorId());
    }
}
