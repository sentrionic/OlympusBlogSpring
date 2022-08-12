package com.github.sentrionic.olympusblog.repository;

import com.github.sentrionic.olympusblog.model.Article;
import com.github.sentrionic.olympusblog.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByArticle(Article article);
    List<Comment> findByArticleOrderByCreatedAtDesc(Article article);
}
