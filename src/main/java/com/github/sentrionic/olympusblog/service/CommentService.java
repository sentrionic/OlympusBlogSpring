package com.github.sentrionic.olympusblog.service;

import com.github.sentrionic.olympusblog.dto.comment.CommentDto;
import com.github.sentrionic.olympusblog.dto.comment.CreateCommentDto;
import com.github.sentrionic.olympusblog.exception.ArticleNotFoundException;
import com.github.sentrionic.olympusblog.exception.CommentNotFoundException;
import com.github.sentrionic.olympusblog.exception.UnauthorizedException;
import com.github.sentrionic.olympusblog.mapper.CommentMapper;
import com.github.sentrionic.olympusblog.mapper.ProfileMapper;
import com.github.sentrionic.olympusblog.model.Comment;
import com.github.sentrionic.olympusblog.repository.ArticleRepository;
import com.github.sentrionic.olympusblog.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final ProfileMapper profileMapper;
    private final CommentMapper commentMapper;
    private final AuthService authService;

    @Transactional
    public CommentDto createComment(String slug, CreateCommentDto input) {
        var article = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new ArticleNotFoundException("Article with slug " + slug + " not found"));
        var user = authService.getCurrentUser();

        var comment = new Comment();
        comment.setBody(input.getBody());
        comment.setAuthor(user);
        comment.setArticle(article);
        commentRepository.save(comment);

        return commentMapper.mapEntityToDto(comment, profileMapper.mapEntityToDto(user, null));
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsBySlug(String slug) {
        var article = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new ArticleNotFoundException("Article with slug " + slug + " not found"));
        var user = authService.getOptionalUser();

        var comments = commentRepository.findByArticleOrderByCreatedAtDesc(article);
        return comments.stream()
                .map(c -> commentMapper.mapEntityToDto(c, profileMapper.mapEntityToDto(c.getAuthor(), user)))
                .toList();
    }

    public CommentDto deleteComment(Long id) {
        var user = authService.getCurrentUser();

        var comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException("Comment with ID " + id + " not found"));

        if (comment.getAuthor() != user) throw new UnauthorizedException();

        commentRepository.delete(comment);
        return commentMapper.mapEntityToDto(comment, profileMapper.mapEntityToDto(user, null));
    }
}
