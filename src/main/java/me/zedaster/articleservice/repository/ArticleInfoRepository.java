package me.zedaster.articleservice.repository;

import me.zedaster.articleservice.entity.ArticleInfo;
import me.zedaster.articleservice.entity.Creator;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for working with data of articles in the DB
 */
@Repository
public interface ArticleInfoRepository extends CrudRepository<ArticleInfo, Long> {
    List<ArticleInfo> findAllByOrderByCreatedAtDesc(Pageable pageable);

    List<ArticleInfo> findAllByCreatorOrderByCreatedAtDesc(Creator creator, Pageable pageable);

    boolean existsByCreatorAndTitle(Creator creator, String title);
}
