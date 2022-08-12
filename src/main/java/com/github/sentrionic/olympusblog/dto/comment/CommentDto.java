package com.github.sentrionic.olympusblog.dto.comment;

import com.github.sentrionic.olympusblog.dto.user.Profile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private Long id;
    private String body;
    private Profile author;
    private String createdAt;
    private String updatedAt;
}
