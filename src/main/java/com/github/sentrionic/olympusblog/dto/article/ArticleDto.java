package com.github.sentrionic.olympusblog.dto.article;

import com.github.sentrionic.olympusblog.dto.user.Profile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleDto {
    private Long id;
    private String slug;
    private String title;
    private String description;
    private String body;
    private String image;
    private List<String> tagList;
    private String createdAt;
    private String updatedAt;
    private boolean favorited = false;
    private boolean bookmarked = false;
    private int favoritesCount = 0;
    private Profile author;
}
