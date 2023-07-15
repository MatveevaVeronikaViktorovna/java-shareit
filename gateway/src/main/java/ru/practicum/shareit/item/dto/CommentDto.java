package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class CommentDto {
    Long id;
    @Size(max = 1024)
    @NotBlank(groups = Create.class)
    String text;
    String authorName;
    LocalDateTime created;
}
