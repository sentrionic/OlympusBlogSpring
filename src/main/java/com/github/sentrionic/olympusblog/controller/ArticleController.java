package com.github.sentrionic.olympusblog.controller;

import com.github.sentrionic.olympusblog.dto.ValidationError;
import com.github.sentrionic.olympusblog.dto.article.ArticleDto;
import com.github.sentrionic.olympusblog.dto.article.ArticleListResponse;
import com.github.sentrionic.olympusblog.dto.article.CreateArticleDTO;
import com.github.sentrionic.olympusblog.dto.article.UpdateArticleDTO;
import com.github.sentrionic.olympusblog.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService service;
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @PostMapping
    public ResponseEntity<Object> createArticle(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) MultipartFile image,
            @RequestParam(required = false) String body,
            @RequestParam(value = "tagList[]", required = false) List<String> tagList
    ) {
        var input = new CreateArticleDTO(title, description, body, image, tagList);
        Set<ConstraintViolation<CreateArticleDTO>> violations = validator.validate(input);
        if (!violations.isEmpty()) {
            var list = violations.stream().map(
                    v -> new ValidationError(v.getPropertyPath().toString(), v.getMessage())
            ).toList();
            return new ResponseEntity<>(list, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(service.createArticle(input), HttpStatus.CREATED);
    }

    @PutMapping("/{slug}")
    public ResponseEntity<Object> updateArticle(
            @PathVariable String slug,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) MultipartFile image,
            @RequestParam(required = false) String body,
            @RequestParam(value = "tagList[]", required = false) List<String> tagList
    ) {
        var input = new UpdateArticleDTO(title, description, body, image, tagList);
        Set<ConstraintViolation<UpdateArticleDTO>> violations = validator.validate(input);
        if (!violations.isEmpty()) {
            var list = violations.stream().map(
                    v -> new ValidationError(v.getPropertyPath().toString(), v.getMessage())
            ).toList();
            return new ResponseEntity<>(list, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(service.updateArticle(slug, input), HttpStatus.CREATED);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ArticleListResponse getAllArticles(
            @RequestParam(required = false, defaultValue = "10") int limit,
            @RequestParam(required = false, defaultValue = "0") int p,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String favorited,
            @RequestParam(required = false) String order,
            @RequestParam(required = false) String cursor
    ) {
        return service.getAllArticles(limit, p, search, order, tag, author, favorited, cursor);
    }

    @GetMapping("/feed")
    @ResponseStatus(HttpStatus.OK)
    public ArticleListResponse getFeed(
            @RequestParam(required = false, defaultValue = "10") int limit,
            @RequestParam(required = false, defaultValue = "0") int p,
            @RequestParam(required = false) String cursor
    ) {
        return service.getFeed(limit, p, cursor);
    }

    @GetMapping("/bookmarked")
    @ResponseStatus(HttpStatus.OK)
    public ArticleListResponse getBookmarked(
            @RequestParam(required = false, defaultValue = "10") int limit,
            @RequestParam(required = false, defaultValue = "0") int p,
            @RequestParam(required = false) String cursor
    ) {
        return service.getBookmarked(limit, p, cursor);
    }

    @GetMapping("/tags")
    @ResponseStatus(HttpStatus.OK)
    public List<String> getTags() {
        return service.getTags();
    }

    @GetMapping("/{slug}")
    @ResponseStatus(HttpStatus.OK)
    public ArticleDto getArticle(@PathVariable String slug) {
        return service.getArticleBySlug(slug);
    }

    @PostMapping("/{slug}/favorite")
    @ResponseStatus(HttpStatus.OK)
    public ArticleDto favoriteArticle(@PathVariable String slug) {
        return service.favoriteArticle(slug);
    }

    @DeleteMapping("/{slug}/favorite")
    @ResponseStatus(HttpStatus.OK)
    public ArticleDto unfavoriteArticle(@PathVariable String slug) {
        return service.unfavoriteArticle(slug);
    }

    @PostMapping("/{slug}/bookmark")
    @ResponseStatus(HttpStatus.OK)
    public ArticleDto bookmarkArticle(@PathVariable String slug) {
        return service.bookmarkArticle(slug);
    }

    @DeleteMapping("/{slug}/bookmark")
    @ResponseStatus(HttpStatus.OK)
    public ArticleDto unbookmarkArticle(@PathVariable String slug) {
        return service.unbookmarkArticle(slug);
    }

    @DeleteMapping("/{slug}")
    @ResponseStatus(HttpStatus.OK)
    public ArticleDto deleteArticle(@PathVariable String slug) {
        return service.deleteArticle(slug);
    }
}
