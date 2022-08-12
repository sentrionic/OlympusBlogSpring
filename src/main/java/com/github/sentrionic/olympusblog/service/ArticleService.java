package com.github.sentrionic.olympusblog.service;

import com.github.sentrionic.olympusblog.dto.article.ArticleDto;
import com.github.sentrionic.olympusblog.dto.article.ArticleListResponse;
import com.github.sentrionic.olympusblog.dto.article.CreateArticleDTO;
import com.github.sentrionic.olympusblog.dto.article.UpdateArticleDTO;
import com.github.sentrionic.olympusblog.exception.ArticleNotFoundException;
import com.github.sentrionic.olympusblog.exception.InvalidCursorException;
import com.github.sentrionic.olympusblog.exception.UnauthorizedException;
import com.github.sentrionic.olympusblog.mapper.ArticleMapper;
import com.github.sentrionic.olympusblog.mapper.ProfileMapper;
import com.github.sentrionic.olympusblog.model.Article;
import com.github.sentrionic.olympusblog.model.Tag;
import com.github.sentrionic.olympusblog.model.User;
import com.github.sentrionic.olympusblog.repository.ArticleRepository;
import com.github.sentrionic.olympusblog.repository.CommentRepository;
import com.github.sentrionic.olympusblog.repository.TagRepository;
import com.github.sentrionic.olympusblog.repository.UserRepository;
import com.github.sentrionic.olympusblog.repository.specifications.ArticlesSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final CommentRepository commentRepository;
    private final AuthService authService;
    private final FileUploadService uploadService;
    private final ArticleMapper articleMapper;
    private final ProfileMapper profileMapper;

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    private static final Pattern EDGESDHASHES = Pattern.compile("(^-|-$)");
    private final Random random = new Random();

    @Transactional
    public ArticleDto getArticleBySlug(String slug) {
        var article = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new ArticleNotFoundException(slug));
        var user = authService.getOptionalUser();
        var author = profileMapper.mapEntityToDto(article.getAuthor(), user);
        return articleMapper.mapEntityToDto(article, author, user);
    }

    @Transactional
    public ArticleListResponse getAllArticles(
            int limit,
            int page,
            String search,
            String order,
            String tag,
            String authorName,
            String favoritedBy,
            String cursor
    ) {
        if (limit == 0) limit = 10;
        var skip = Math.max(page - 1, 0);
        var sort = Objects.equals(order, "ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        var pageable = PageRequest.of(skip, limit, sort, "createdAt");
        if (search == null) search = "";

        Page<Article> articles;

        Tag tagEntity = null;
        if (tag != null) {
            var result = tagRepository.findByName(tag);
            if (result.isPresent()) tagEntity = result.get();
        }

        User authorEntity = null;
        if (authorName != null) {
            var result = userRepository.findByUsername(authorName);
            if (result.isPresent()) authorEntity = result.get();
        }

        User favoritedByEntity = null;
        if (favoritedBy != null) {
            var result = userRepository.findByUsername(favoritedBy);
            if (result.isPresent()) favoritedByEntity = result.get();
        }

        if (Objects.equals(order, "TOP")) {
            articles = articleRepository.findAllOrderByFavoritesCountDesc(
                    ArticlesSpecifications.queryArticles(
                            tagEntity,
                            authorEntity,
                            favoritedByEntity,
                            cursor
                    ).and(ArticlesSpecifications.searchArticle(search)),
                    pageable
            );
        } else {
            articles = articleRepository.findAll(
                    ArticlesSpecifications.queryArticles(
                            tagEntity,
                            authorEntity,
                            favoritedByEntity,
                            cursor
                    ).and(ArticlesSpecifications.searchArticle(search)),
                    pageable
            );
        }

        var user = authService.getOptionalUser();

        return serializeResponse(articles, user);
    }

    public ArticleListResponse getFeed(
            int limit,
            int page,
            String cursor
    ) {
        if (limit == 0) limit = 10;
        var skip = Math.max(page - 1, 0);
        var pageable = PageRequest.of(skip, limit);
        var user = authService.getCurrentUser();

        var articles = articleRepository.findFollowingArticles(
                user.getId(),
                cursor,
                pageable
        );

        return serializeResponse(articles, user);
    }

    private ArticleListResponse serializeResponse(Page<Article> articles, User user) {
        var results = articles.toList().stream().map(a -> {
            var author = profileMapper.mapEntityToDto(a.getAuthor(), user);
            return articleMapper.mapEntityToDto(a, author, user);
        }).toList();

        return new ArticleListResponse(
                results,
                articles.hasNext()
        );
    }

    public ArticleListResponse getBookmarked(
            int limit,
            int page,
            String cursor
    ) {
        if (limit == 0) limit = 10;
        var skip = Math.max(page - 1, 0);
        var pageable = PageRequest.of(skip, limit);
        var user = authService.getCurrentUser();

        var articles = articleRepository.findByBookmarksOrderByCreatedAtDesc(
                user.getId(),
                cursor,
                pageable
        );

        return serializeResponse(articles, user);
    }

    public ArticleDto createArticle(CreateArticleDTO input) {
        var url = String.format("https://picsum.photos/seed/%s/1080", getRandomString(12));
        var user = authService.getCurrentUser();

        if (input.getImage() != null) {
            var directory = String.format("spring/%s/%s", user.getId(), getRandomString(16));
            try {
                url = uploadService.uploadArticleImage(input.getImage(), directory);
            } catch (IOException e) {
                throw new InvalidCursorException();
            }
        }

        var tagList = upsertTags(input.getTagList());

        var article = new Article();
        article.setTitle(input.getTitle());
        article.setSlug(generateSlug(input.getTitle()));
        article.setDescription(input.getDescription());
        article.setBody(input.getBody());
        article.setImage(url);
        article.setTagList(tagList);
        article.setAuthor(user);

        articleRepository.save(article);
        var author = profileMapper.mapEntityToDto(user, null);
        return articleMapper.mapEntityToDto(article, author, null);
    }

    public ArticleDto favoriteArticle(String slug) {
        var article = findArticleForSlug(slug);
        var user = authService.getCurrentUser();

        if (user != null && !article.getFavorites().contains(user)) {
            var favorites = article.getFavorites();
            favorites.add(user);
            article.setFavorites(favorites);
            articleRepository.save(article);
        }

        var author = profileMapper.mapEntityToDto(article.getAuthor(), user);
        return articleMapper.mapEntityToDto(article, author, user);
    }

    public ArticleDto unfavoriteArticle(String slug) {
        var article = findArticleForSlug(slug);
        var user = authService.getCurrentUser();

        if (user != null && article.getFavorites().contains(user)) {
            var favorites = article.getFavorites();
            favorites.remove(user);
            article.setFavorites(favorites);
            articleRepository.save(article);
        }

        var author = profileMapper.mapEntityToDto(article.getAuthor(), user);
        return articleMapper.mapEntityToDto(article, author, user);
    }

    public ArticleDto bookmarkArticle(String slug) {
        var article = findArticleForSlug(slug);
        var user = authService.getCurrentUser();

        if (user != null && !article.getBookmarks().contains(user)) {
            var bookmarks = article.getBookmarks();
            bookmarks.add(user);
            article.setBookmarks(bookmarks);
            articleRepository.save(article);
        }

        var author = profileMapper.mapEntityToDto(article.getAuthor(), user);
        return articleMapper.mapEntityToDto(article, author, user);
    }

    public ArticleDto unbookmarkArticle(String slug) {
        var article = findArticleForSlug(slug);
        var user = authService.getCurrentUser();

        if (user != null && article.getBookmarks().contains(user)) {
            var bookmarks = article.getBookmarks();
            bookmarks.remove(user);
            article.setBookmarks(bookmarks);
            articleRepository.save(article);
        }

        var author = profileMapper.mapEntityToDto(article.getAuthor(), user);
        return articleMapper.mapEntityToDto(article, author, user);
    }

    public ArticleDto updateArticle(String slug, UpdateArticleDTO input) {

        var article = findArticleForSlug(slug);
        var user = authService.getCurrentUser();

        if (article.getAuthor() != user) throw new UnauthorizedException();

        var tagList = upsertTags(input.getTagList());

        article.setTitle(input.getTitle());
        article.setDescription(input.getDescription());
        article.setBody(article.getBody());
        article.setTagList(tagList);

        if (input.getImage() != null) {
            var directory = String.format("spring/%s/%s", user.getId(), getRandomString(16));
            try {
                var url = uploadService.uploadArticleImage(input.getImage(), directory);
                article.setImage(url);
            } catch (IOException e) {
                throw new InvalidCursorException();
            }
        }

        articleRepository.save(article);
        var author = profileMapper.mapEntityToDto(article.getAuthor(), user);
        return articleMapper.mapEntityToDto(article, author, user);
    }

    @Transactional
    public ArticleDto deleteArticle(String slug) {
        var article = findArticleForSlug(slug);
        var user = authService.getCurrentUser();

        if (article.getAuthor() != user) throw new UnauthorizedException();

        commentRepository.deleteAll(commentRepository.findByArticle(article));
        articleRepository.delete(article);

        var author = profileMapper.mapEntityToDto(article.getAuthor(), user);
        return articleMapper.mapEntityToDto(article, author, user);
    }

    public List<String> getTags() {
        return tagRepository.findAll(PageRequest.of(0, 10)).map(Tag::getName).stream().toList();
    }

    private Article findArticleForSlug(String slug) {
        return articleRepository.findBySlug(slug)
                .orElseThrow(() -> new ArticleNotFoundException(slug));
    }

    private Set<Tag> upsertTags(List<String> tags) {
        return tags.stream().map(t -> {
            var tag = tagRepository.findByName(t);
            if (tag.isPresent()) return tag.get();
            var newTag = new Tag();
            newTag.setName(t);
            return tagRepository.save(newTag);
        }).collect(Collectors.toSet());
    }

    private String generateSlug(String input) {
        String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        slug = EDGESDHASHES.matcher(slug).replaceAll("");
        slug += "-" + getRandomString(6);
        return slug.toLowerCase(Locale.ENGLISH);
    }

    private String getRandomString(int size) {
        return random.ints(48, 122)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(size)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
