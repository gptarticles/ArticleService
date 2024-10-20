package me.zedaster.articleservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import me.zedaster.articleservice.dto.article.CreatorData;

/**
 * Row of creator in the database
 */
@Entity
@Table(name = "creators")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "creatorId")
public class Creator {
    /**
     * User ID of the creator
     */
    @Id
    @Column(unique = true)
    @Getter
    @Setter
    private Long creatorId;

    /**
     * Username of the creator
     */
    @Column(unique = true)
    private String username;

    public CreatorData toCreatorData() {
        return new CreatorData(getUsername());
    }
}
