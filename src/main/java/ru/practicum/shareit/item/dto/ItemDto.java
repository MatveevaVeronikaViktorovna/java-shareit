package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.user.dto.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class ItemDto {
    Long id;
    @Size(max = 128)
    @NotBlank(groups = Create.class)
    String name;
    @Size(max = 1024)
    @NotBlank(groups = Create.class)
    String description;
    @NotNull(groups = Create.class)
    Boolean available;
    BookingDtoForItem lastBooking;
    BookingDtoForItem nextBooking;
    List<CommentDto> comments;
}
