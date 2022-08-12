package com.github.sentrionic.olympusblog.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Profile {
    private Long id;
    private String username;
    private String bio;
    private String image;
    private int followers = 0;
    private int followee = 0;
    private boolean following = false;
}
