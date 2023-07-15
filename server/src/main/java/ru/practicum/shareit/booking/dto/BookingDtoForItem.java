package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class BookingDtoForItem {
    Long id;
    LocalDateTime start;
    LocalDateTime end;
    Long bookerId;
    Status status;
}
