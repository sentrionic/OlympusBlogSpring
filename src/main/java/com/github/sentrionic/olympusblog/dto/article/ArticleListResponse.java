package com.github.sentrionic.olympusblog.dto.article;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleListResponse {
    private List<ArticleDto> articles;
    private boolean hasMore;
}
