package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.dto.Create;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class ItemRequestDto {
    Long id;
    @Size(max = 1024)
    @NotBlank(groups = Create.class)
    String description;
    UserDto requestor;
    LocalDateTime created;
}
