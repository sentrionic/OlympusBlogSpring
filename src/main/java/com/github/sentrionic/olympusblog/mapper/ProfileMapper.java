package com.github.sentrionic.olympusblog.mapper;

import com.github.sentrionic.olympusblog.dto.user.Profile;
import com.github.sentrionic.olympusblog.model.User;
import com.github.sentrionic.olympusblog.repository.UserRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class ProfileMapper {
    @Autowired
    private UserRepository repository;

    @Mapping(target = "id", source = "profile.id")
    @Mapping(target = "username", source = "profile.username")
    @Mapping(target = "bio", source = "profile.bio")
    @Mapping(target = "image", source = "profile.image")
    @Mapping(target = "following", expression = "java(isFollowing(profile, currentUser))")
    @Mapping(target = "followers", expression = "java(getFollowerCount(profile))")
    @Mapping(target = "followee", expression = "java(getFolloweeCount(profile))")
    public abstract Profile mapEntityToDto(User profile, User currentUser);

    boolean isFollowing(User profile, User currentUser) {
        if (currentUser == null) return false;
        return profile.getFollowers().contains(currentUser);
    }

    int getFollowerCount(User profile) {
        return profile.getFollowers().size();
    }

    int getFolloweeCount(User profile) {
        return repository.getFolloweeCount(profile.getId());
    }
}
