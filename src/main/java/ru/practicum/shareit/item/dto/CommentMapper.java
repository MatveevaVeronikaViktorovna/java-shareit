package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.item.model.Comment;

@Data
public class CommentMapper {
    public static Comment toComment(CommentDto dto) {
        if (dto == null) return null;
        else {
            Comment comment = new Comment();
            if (dto.getText() != null) {
                comment.setText(dto.getText());
            }
            return comment;
        }
    }

    public static CommentDto toDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setAuthorName(comment.getAuthor().getName());
        commentDto.setCreated(comment.getCreated());
        return commentDto;
    }

}
