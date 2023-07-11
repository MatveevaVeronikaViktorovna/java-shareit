package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.model.Comment;

@Mapper
public interface CommentDtoMapper {
    Comment dtoToComment(CommentDto dto);
    CommentDto commentToDto(Comment comment);
}
