package com.github.sentrionic.olympusblog.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthDto {
    private Long id;
    private String username;
    private String email;
    private String image;
    private String bio;
    private String createdAt;
    private String updatedAt;
}
