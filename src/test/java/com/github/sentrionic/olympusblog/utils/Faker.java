package com.github.sentrionic.olympusblog.utils;

import com.github.sentrionic.olympusblog.dto.article.ArticleDto;
import com.github.sentrionic.olympusblog.dto.comment.CommentDto;
import com.github.sentrionic.olympusblog.dto.user.Profile;
import com.github.sentrionic.olympusblog.model.Article;
import com.github.sentrionic.olympusblog.model.Comment;
import com.github.sentrionic.olympusblog.model.Tag;
import com.github.sentrionic.olympusblog.model.User;
import org.springframework.lang.Nullable;

import java.util.Random;
import java.util.Set;

public class Faker {
    public static User generateUser() {
        var user = new User();
        user.setUsername(getRandomString(10));
        user.setEmail(getRandomString(6) + "@example.com");
        user.setId(1L);
        return user;
    }

    public static Article generateArticle() {
        return generateArticle(Faker.generateUser());
    }

    public static Article generateArticle(@Nullable User author) {
        var article = new Article();
        article.setId(1L);
        article.setSlug(getRandomString(6));
        article.setBody(getRandomString(150));
        article.setDescription(getRandomString(20));
        article.setTitle(getRandomString(10));
        article.setTagList(Set.of(generateTag(), generateTag()));
        if (author != null) article.setAuthor(author);
        return article;
    }

    public static Tag generateTag() {
        return new Tag(1L, getRandomString(10));
    }

    public static Comment generateComment(User author, Article article) {
        var comment = new Comment();
        comment.setBody(getRandomString(100));
        comment.setAuthor(author);
        comment.setArticle(article);
        return comment;
    }

    public static Profile generateProfile() {
        return generateProfile(generateUser());
    }

    public static Profile generateProfile(User user) {
        return new Profile(
                user.getId(),
                user.getUsername(),
                user.getBio(),
                user.getImage(),
                user.getFollowers().size(),
                0,
                false
        );
    }

    public static CommentDto generateCommentDto() {
        return generateCommentDto(generateComment(generateUser(), null));
    }

    public static CommentDto generateCommentDto(Article article) {
        return generateCommentDto(generateComment(generateUser(), article));
    }

    public static CommentDto generateCommentDto(User author) {
        return generateCommentDto(generateComment(author, null));
    }

    public static CommentDto generateCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getBody(),
                generateProfile(comment.getAuthor()),
                comment.getCreatedAt().toString(),
                comment.getUpdatedAt().toString()
        );
    }

    private static final Random random = new Random();

    private static String getRandomString(int size) {
        return random.ints(48, 122)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(size)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
