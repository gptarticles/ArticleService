package me.zedaster.articleservice.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import me.zedaster.articleservice.dto.article.Article;
import me.zedaster.articleservice.dto.article.ArticleData;
import me.zedaster.articleservice.dto.article.ArticleSummary;
import me.zedaster.articleservice.entity.ArticleInfo;
import me.zedaster.articleservice.entity.Creator;
import me.zedaster.articleservice.repository.ArticleInfoRepository;
import me.zedaster.articleservice.repository.CreatorRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Service for handling operations with articles
 */
@Service
@Validated
@RequiredArgsConstructor
public class ArticleService {
    /**
     * Number of articles on one page for recent articles
     */
    private static final int RECENT_ARTICLES_PAGE_SIZE = 20;

    /**
     * Number of articles on one page for user articles
     */
    private static final int USER_ARTICLES_PAGE_SIZE = 20;

    private static final String INCORRECT_USER_ID = "User id must be bigger than zero!";
    private static final String INCORRECT_PAGE_NUMBER = "Page number must be bigger than zero!";

    private static final String INCORRECT_ARTICLE_ID = "Article id must be bigger than zero!";

    private static final String ARTICLE_DATA_NOT_NULL = "Article data must be not null!";

    private final ArticleInfoRepository articleInfoRepository;

    private final CreatorRepository creatorRepository;

    private final ContentService contentService;

    /**
     * Gets a certain article from a storage
     * @param id ID of the article
     * @return Optional object with article or nothing if article with the ID doesn't exist
     */
    public Optional<Article> getArticle(@Min(value = 1, message = INCORRECT_ARTICLE_ID) long id) {
        Optional<ArticleInfo> info = articleInfoRepository.findById(id);
        if (info.isEmpty()) {
            return Optional.empty();
        }

        String content;
        try {
            content = contentService.getContentByArticleId(id).orElseThrow();
        } catch (ContentServiceException e) {
            throw new InternalServerException("Can't get content of the article!", e);
        } catch (NoSuchElementException e) {
            throw new InternalServerException("ArticleInfo exists, but there's no content for the article!", e);
        }
        return Optional.of(new Article(info.get(), content));
    }

    /**
     * Gets summaries of articles that has been published recently
     * @param pageNumber Number of page (starts from 1)
     * @return List of article summaries
     */
    public List<ArticleSummary> getRecentArticleSummaries(
            @Min(value = 1, message = INCORRECT_PAGE_NUMBER) int pageNumber) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, RECENT_ARTICLES_PAGE_SIZE);
        List<ArticleInfo> articleInfos = articleInfoRepository.findAllByOrderByCreatedAtDesc(pageRequest);
        return ArticleSummary.fromArticleInfos(articleInfos);
    }

    /**
     * Gets summaries of articles that are published by user with the specified ID.
     * @param userId specified ID of the user
     * @param pageNumber Number of page (starts from 1)
     * @return List of article summaries
     */
    public List<ArticleSummary> getArticleSummariesByUserId(
            @Min(value = 1, message = INCORRECT_USER_ID) long userId,
            @Min(value = 1, message = INCORRECT_PAGE_NUMBER) int pageNumber) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, USER_ARTICLES_PAGE_SIZE);
        Creator creator = new Creator(userId, null);
        List<ArticleInfo> articleInfos = articleInfoRepository.findAllByCreatorOrderByCreatedAtDesc(creator, pageRequest);
        return ArticleSummary.fromArticleInfos(articleInfos);
    }

    /**
     * Creates a new article
     * @param userId ID of user that is creating the article
     * @param articleData Data for the new article
     * @return ID of the new article
     */
    public long createArticle(
            @Min(value = 1, message = INCORRECT_USER_ID) long userId,
            @NotNull(message = ARTICLE_DATA_NOT_NULL) @Valid ArticleData articleData) throws ArticleServiceException {
        Creator creator = creatorRepository.findById(userId).orElseThrow(() ->
                new ArticleServiceException("User with the ID doesn't exist!"));
        if (articleInfoRepository.existsByCreatorAndTitle(creator, articleData.getTitle())) {
            throw new ArticleServiceException("User already has an article with the same title!");
        }
        ArticleInfo info = articleInfoRepository.save(new ArticleInfo(articleData.getTitle(), Instant.now(), creator));
        try {
            contentService.saveContent(info.getId(), articleData.getContent());
        } catch (ContentServiceException e) {
            articleInfoRepository.deleteById(info.getId());
            throw new InternalServerException("Failed to save content for the article!", e);
        }
        return info.getId();
    }

    /**
     * Updates an existing article
     * @param articleId ID of the article
     * @param articleData New data for the article
     */
    public void updateArticle(
            @Min(value = 1, message = INCORRECT_ARTICLE_ID) long articleId,
            @NotNull(message = ARTICLE_DATA_NOT_NULL) @Valid ArticleData articleData) throws ArticleServiceException {
        ArticleInfo oldInfo = articleInfoRepository.findById(articleId).orElseThrow(() ->
                new ArticleServiceException("Article with the specified ID doesn't exist!"));

        if (articleInfoRepository.existsByCreatorAndTitle(oldInfo.getCreator(), articleData.getTitle())) {
            throw new ArticleServiceException("User already has an article with the same title!");
        }

        ArticleInfo newInfo = oldInfo.copy();
        newInfo.setTitle(articleData.getTitle());
        articleInfoRepository.save(newInfo);

        try {
            contentService.saveContent(oldInfo.getId(), articleData.getContent());
        } catch (ContentServiceException e) {
            articleInfoRepository.save(oldInfo);
            throw new InternalServerException("Failed to update content for the article!", e);
        }
    }
}
