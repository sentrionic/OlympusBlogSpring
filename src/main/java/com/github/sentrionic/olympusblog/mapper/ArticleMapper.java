package com.github.sentrionic.olympusblog.mapper;

import com.github.sentrionic.olympusblog.dto.article.ArticleDto;
import com.github.sentrionic.olympusblog.dto.user.Profile;
import com.github.sentrionic.olympusblog.model.Article;
import com.github.sentrionic.olympusblog.model.Tag;
import com.github.sentrionic.olympusblog.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ArticleMapper {
    @Mapping(target = "tagList", expression = "java(mapTags(article))")
    @Mapping(target = "id", source = "article.id")
    @Mapping(target = "image", source = "article.image")
    @Mapping(target = "createdAt", source = "article.createdAt")
    @Mapping(target = "updatedAt", source = "article.updatedAt")
    @Mapping(target = "author", source = "author")
    @Mapping(target = "favorited", expression = "java(isFavorited(article, currentUser))")
    @Mapping(target = "bookmarked", expression = "java(isBookmarked(article, currentUser))")
    @Mapping(target = "favoritesCount", expression = "java(getFavoritesCount(article))")
    ArticleDto mapEntityToDto(Article article, Profile author, User currentUser);

    default List<String> mapTags(Article article) {
        return article.getTagList().stream().map(Tag::getName).toList();
    }

    default boolean isFavorited(Article article, User currentUser) {
        if (currentUser == null) return false;
        return article.getFavorites().contains(currentUser);
    }

    default boolean isBookmarked(Article article, User currentUser) {
        if (currentUser == null) return false;
        return article.getBookmarks().contains(currentUser);
    }

    default int getFavoritesCount(Article article) {
        return article.getFavorites().size();
    }
}
