package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.valid.StartBeforeEndDateValid;

import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@StartBeforeEndDateValid
public class BookingDto {
    LocalDateTime start;
    LocalDateTime end;
    Long itemId;
}
