package com.github.sentrionic.olympusblog.controller;

import com.github.sentrionic.olympusblog.dto.comment.CommentDto;
import com.github.sentrionic.olympusblog.dto.comment.CreateCommentDto;
import com.github.sentrionic.olympusblog.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class CommentsController {
    private final CommentService service;

    @PostMapping("/{slug}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@PathVariable String slug, @Valid @RequestBody CreateCommentDto input) {
        return service.createComment(slug, input);
    }

    @GetMapping("/{slug}/comments")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getAllComments(@PathVariable String slug) {
        return service.getCommentsBySlug(slug);
    }

    @DeleteMapping("/{slug}/comments/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto deleteComment(@PathVariable String slug, @PathVariable Long id) {
        return service.deleteComment(id);
    }

}
