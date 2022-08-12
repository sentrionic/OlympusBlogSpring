package com.github.sentrionic.olympusblog.dto.article;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateArticleDTO {
    @NotEmpty
    @Size(min = 10, max = 100, message = "Title must be between 10 and 100 characters")
    private String title;

    @NotEmpty
    @Size(min = 10, max = 150, message = "Description must be between 10 and 100 characters")
    private String description;

    @NotEmpty(message = "Body must not be empty")
    private String body;

    @Nullable
    private MultipartFile image;

    @NotNull
    @Size(min = 1, max = 5)
    private List<@NotNull @Size(min = 3, max = 15) String> tagList;
}
