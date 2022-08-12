package com.github.sentrionic.olympusblog.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "articles")
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Title cannot be empty or Null")
    @Size(min = 3, max = 100)
    private String title;

    @NotBlank(message = "Description cannot be empty or Null")
    @Size(min = 3, max = 150)
    private String description;

    @NotBlank(message = "Body cannot be empty or Null")
    @Lob
    private String body;

    @NotBlank
    @Column(unique = true)
    private String slug;

    private String image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authorId", referencedColumnName = "id")
    private User author;

    @ManyToMany
    @JoinTable(name = "article_favorites")
    private Set<User> favorites = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "article_bookmarks")
    private Set<User> bookmarks = new HashSet<>();

    @ManyToMany
    private Set<Tag> tagList = new HashSet<>();

    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();

    @PreRemove
    public void cleanSets() {
        favorites.clear();
        bookmarks.clear();
        tagList.clear();
    }
}
