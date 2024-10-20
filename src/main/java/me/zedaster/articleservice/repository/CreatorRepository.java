package me.zedaster.articleservice.repository;

import me.zedaster.articleservice.entity.Creator;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for working with creators
 */
@Repository
public interface CreatorRepository extends CrudRepository<Creator, Long> {
}
