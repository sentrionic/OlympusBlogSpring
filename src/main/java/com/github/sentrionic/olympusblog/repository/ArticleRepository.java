package com.github.sentrionic.olympusblog.repository;

import com.github.sentrionic.olympusblog.model.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public
interface ArticleRepository extends JpaRepository<Article, Long>, JpaSpecificationExecutor<Article> {
    Optional<Article> findBySlug(String slug);

    @Query(
            value = "select a from Article a left join a.favorites order by a.favorites.size desc"
    )
    Page<Article> findAllOrderByFavoritesCountDesc(Specification<Article> specification, Pageable pageable);

    @Query(
            nativeQuery = true,
            value = """
                        SELECT a.*
                        FROM articles a left outer join users on a.author_id = users.id left outer join user_followings uf on users.id = uf.user_id
                        where uf.followers_id = :id
                        AND :cursor IS NULL OR a.created_at < cast(:cursor as timestamp)
                        order by created_at DESC
                    """
    )
    Page<Article> findFollowingArticles(@Param("id") Long id, @Param("cursor") @Nullable String cursor, Pageable pageable);

    @Query(
            nativeQuery = true,
            value = """
                        SELECT a.*
                        FROM articles a FULL OUTER JOIN article_bookmarks ab ON a.id = ab.article_id
                        WHERE ab.bookmarks_id = :userId
                            AND :cursor IS NULL OR a.created_at < cast(:cursor as timestamp)
                        order by created_at DESC
                    """
    )
    Page<Article> findByBookmarksOrderByCreatedAtDesc(@Param("userId") Long userId, @Param("cursor") @Nullable String cursor, Pageable pageable);
}
