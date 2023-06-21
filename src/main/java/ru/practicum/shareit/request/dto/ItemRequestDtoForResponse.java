package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.dto.ItemDtoForItemRequest;

import java.time.LocalDateTime;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class ItemRequestDtoForResponse {
    Long id;
    String description;
    LocalDateTime created;
    List<ItemDtoForItemRequest> items;
}
