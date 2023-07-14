package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class ItemDto {
    Long id;
    String name;
    String description;
    Boolean available;
    BookingDtoForItem lastBooking;
    BookingDtoForItem nextBooking;
    List<CommentDto> comments;
    Long requestId;
}
