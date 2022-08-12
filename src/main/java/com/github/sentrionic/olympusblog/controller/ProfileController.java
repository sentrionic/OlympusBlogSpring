package com.github.sentrionic.olympusblog.controller;

import com.github.sentrionic.olympusblog.dto.user.Profile;
import com.github.sentrionic.olympusblog.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Profile> getProfiles(@RequestParam(required = false) String search) {
        return service.getProfiles(search);
    }

    @GetMapping("/{username}")
    public Profile getProfileByUsername(@PathVariable String username) {
        return service.getProfileByUsername(username);
    }

    @PostMapping("/{username}/follow")
    public Profile followProfile(@PathVariable String username) {
        return service.followProfile(username);
    }

    @DeleteMapping("/{username}/follow")
    public Profile unfollowProfile(@PathVariable String username) {
        return service.unfollowProfile(username);
    }
}
