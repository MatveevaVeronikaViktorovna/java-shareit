package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.dto.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class ItemDto {
    Long id;
    @NotBlank(groups = Create.class)
    String name;
    @NotBlank(groups = Create.class)
    String description;
    @NotNull(groups = Create.class)
    Boolean available;
}
