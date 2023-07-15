package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface CommentDtoMapper {
    Comment dtoToComment(CommentDto dto);

    @Mapping(target = "authorName", source = "author")
    CommentDto commentToDto(Comment comment);

    default String mapAuthorToAuthorName(User author) {
        return author.getName();
    }

}
