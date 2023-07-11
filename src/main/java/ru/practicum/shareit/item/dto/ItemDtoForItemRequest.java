package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class ItemDtoForItemRequest {
    Long id;
    String name;
    String description;
    Boolean available;
    Long requestId;
}
