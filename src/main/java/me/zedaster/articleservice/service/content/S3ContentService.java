package me.zedaster.articleservice.service.content;

import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of ContentService with AWS SDK that stores content of articles in S3-comparable object storages.
 */
@Service
@NoArgsConstructor
@Primary
public class S3ContentService implements ContentService {
    @Override
    public Optional<String> getContentByArticleId(long articleId) {
        // TODO
        return Optional.empty();
    }

    @Override
    public void saveContent(long articleId, String content) {
        // TODO
    }
}
