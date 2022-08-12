package com.github.sentrionic.olympusblog.repository.specifications;

import com.github.sentrionic.olympusblog.model.Article;
import com.github.sentrionic.olympusblog.model.Tag;
import com.github.sentrionic.olympusblog.model.User;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;

public class ArticlesSpecifications {

    private ArticlesSpecifications() {
    }

    public static Specification<Article> queryArticles(Tag tag, User author, User favoritedBy, String cursor) {
        return (root, query, criteriaBuilder) -> {
            var predicates = new ArrayList<Predicate>();

            if (author != null) {
                var user = root.<User>get("author");
                predicates.add(criteriaBuilder.equal(user, author));
            }

            if (tag != null) {
                var tagList = root.<Collection<Tag>>get("tagList");
                predicates.add(criteriaBuilder.isMember(tag, tagList));
            }

            if (favoritedBy != null) {
                var favorited = root.<Collection<User>>get("favorites");
                predicates.add(criteriaBuilder.isMember(favoritedBy, favorited));
            }

            if (cursor != null) {
                var createdAt = root.<Instant>get("createdAt");
                predicates.add(criteriaBuilder.lessThan(createdAt, Instant.parse(cursor)));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Article> searchArticle(String search) {
        var queryString = "%" + (search == null ? "" : search.toLowerCase()) + "%";
        return (root, query, criteriaBuilder) -> criteriaBuilder.or(
                criteriaBuilder.like(root.get("description"), queryString),
                criteriaBuilder.like(root.get("title"), queryString)
        );
    }
}
