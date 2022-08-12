package com.github.sentrionic.olympusblog.service;

import com.github.sentrionic.olympusblog.dto.user.Profile;
import com.github.sentrionic.olympusblog.exception.ProfileNotFoundException;
import com.github.sentrionic.olympusblog.mapper.ProfileMapper;
import com.github.sentrionic.olympusblog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final UserRepository userRepository;
    private final ProfileMapper profileMapper;
    private final AuthService authService;

    public List<Profile> getProfiles(String search) {
        var currentUser = authService.getOptionalUser();
        if (search == null) search = "";
        var users = userRepository.findProfiles(search);
        return users.stream().map(p -> profileMapper.mapEntityToDto(p, currentUser)).toList();
    }

    public Profile getProfileByUsername(String username) {
        var currentUser = authService.getOptionalUser();
        var profile = userRepository.findByUsername(username)
                .orElseThrow(() -> new ProfileNotFoundException("A profile for " + username + " does not exist"));
        return profileMapper.mapEntityToDto(profile, currentUser);
    }

    public Profile followProfile(String username) {
        var currentUser = authService.getCurrentUser();
        var profile = userRepository.findByUsername(username)
                .orElseThrow(() -> new ProfileNotFoundException("A profile for " + username + " does not exist"));

        if (!profile.getFollowers().contains(currentUser)) {
            var followers = profile.getFollowers();
            followers.add(currentUser);
            profile.setFollowers(followers);
            userRepository.save(profile);
        }

        return profileMapper.mapEntityToDto(profile, currentUser);
    }

    public Profile unfollowProfile(String username) {
        var currentUser = authService.getCurrentUser();
        var profile = userRepository.findByUsername(username)
                .orElseThrow(() -> new ProfileNotFoundException("A profile for " + username + " does not exist"));

        if (profile.getFollowers().contains(currentUser)) {
            var followers = profile.getFollowers();
            followers.remove(currentUser);
            profile.setFollowers(followers);
            userRepository.save(profile);
        }

        return profileMapper.mapEntityToDto(profile, currentUser);
    }
}
