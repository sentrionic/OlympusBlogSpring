package com.github.sentrionic.olympusblog.mapper;

import com.github.sentrionic.olympusblog.dto.comment.CommentDto;
import com.github.sentrionic.olympusblog.dto.user.Profile;
import com.github.sentrionic.olympusblog.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "id", source = "comment.id")
    @Mapping(target = "body", source = "comment.body")
    @Mapping(target = "createdAt", source = "comment.createdAt")
    @Mapping(target = "updatedAt", source = "comment.updatedAt")
    @Mapping(target = "author", source = "author")
    CommentDto mapEntityToDto(Comment comment, Profile author);
}
