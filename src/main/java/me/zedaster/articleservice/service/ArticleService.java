package me.zedaster.articleservice.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import me.zedaster.articleservice.dto.article.Article;
import me.zedaster.articleservice.dto.article.ArticleData;
import me.zedaster.articleservice.dto.article.ArticleSummary;
import me.zedaster.articleservice.dto.article.Creator;
import me.zedaster.articleservice.entity.ArticleInfo;
import me.zedaster.articleservice.repository.ArticleInfoRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
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

    private final ContentService contentService;

    private final CreatorService creatorService;

    /**
     * Gets a certain article from a storage
     * @param id ID of the article
     * @return Optional object with article or nothing if article with the ID doesn't exist
     */
    @Transactional(propagation = Propagation.SUPPORTS)
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

        Creator creator = creatorService.getCreator(info.get().getCreatorId());
        return Optional.of(new Article(info.get(), content, creator));
    }

    /**
     * Gets summaries of articles that has been published recently
     * @param pageNumber Number of page (starts from 1)
     * @return List of article summaries
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<ArticleSummary> getRecentArticleSummaries(
            @Min(value = 1, message = INCORRECT_PAGE_NUMBER) int pageNumber) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, RECENT_ARTICLES_PAGE_SIZE);
        List<ArticleInfo> articleInfos = articleInfoRepository.findAllByOrderByCreatedAtDesc(pageRequest);
        List<Creator> creators = creatorService.getCreatorsByIds(articleInfos.stream()
                .map(ArticleInfo::getCreatorId)
                .toList());
        return ArticleSummary.fromArticleInfosAndCreators(articleInfos, creators);
    }

    /**
     * Gets summaries of articles that are published by user with the specified ID.
     * @param creator Creator of the articles
     * @param pageNumber Number of page (starts from 1)
     * @return List of article summaries
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<ArticleSummary> getArticleSummariesByCreator(
            Creator creator,
            @Min(value = 1, message = INCORRECT_PAGE_NUMBER) int pageNumber) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, USER_ARTICLES_PAGE_SIZE);
        List<ArticleInfo> articleInfos = articleInfoRepository.findAllByCreatorIdOrderByCreatedAtDesc(
                creator.getId(), pageRequest);
        return ArticleSummary.fromArticleInfosBySingleCreator(articleInfos, creator);
    }

    /**
     * Creates a new article
     * @param userId ID of user that is creating the article
     * @param articleData Data for the new article
     * @return ID of the new article
     */
    @Transactional(rollbackFor = {ArticleServiceException.class, InternalServerException.class})
    public long createArticle(
            @Min(value = 1, message = INCORRECT_USER_ID) long userId,
            @NotNull(message = ARTICLE_DATA_NOT_NULL) @Valid ArticleData articleData) {
        if (articleInfoRepository.existsByCreatorIdAndTitle(userId, articleData.getTitle())) {
            throw new ArticleServiceException("User already has an article with the same title!");
        }
        ArticleInfo info = articleInfoRepository.save(new ArticleInfo(articleData.getTitle(), Instant.now(), userId));

        try {
            contentService.saveContent(info.getId(), articleData.getContent());
        } catch (ContentServiceException e) {
            throw new InternalServerException("Failed to save content for the article!", e);
        }
        return info.getId();
    }

    /**
     * Updates an existing article
     * @param articleId ID of the article
     * @param articleData New data for the article
     */
    @Transactional(rollbackFor = {ArticleServiceException.class, InternalServerException.class})
    public void updateArticle(
            @Min(value = 1, message = INCORRECT_ARTICLE_ID) long articleId,
            @NotNull(message = ARTICLE_DATA_NOT_NULL) @Valid ArticleData articleData) {
        ArticleInfo oldInfo = articleInfoRepository.findById(articleId).orElseThrow(() ->
                new ArticleServiceException("Article with the specified ID doesn't exist!"));

        ensureUniqueTitle(oldInfo.getCreatorId(), articleId, articleData.getTitle());

        ArticleInfo newInfo = oldInfo.copy();
        newInfo.setTitle(articleData.getTitle());
        articleInfoRepository.save(newInfo);

        try {
            contentService.saveContent(oldInfo.getId(), articleData.getContent());
        } catch (ContentServiceException e) {
            throw new InternalServerException("Failed to update content for the article!", e);
        }
    }

    /**
     * Checks if the user has another article with the same title. If so, throws an exception
     */
    private void ensureUniqueTitle(long creatorId, long articleId, String title) {
        List<ArticleInfo> sameNamedArticles = articleInfoRepository.findAllByCreatorIdAndTitle(creatorId, title);
        if (sameNamedArticles.size() == 1 && !sameNamedArticles.get(0).getId().equals(articleId)) {
            throw new ArticleServiceException("User already has another article with the same title!");
        }
    }
}
