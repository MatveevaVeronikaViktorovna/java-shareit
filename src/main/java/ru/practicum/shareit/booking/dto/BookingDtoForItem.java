package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;

public class BookingDtoForItem {
    Long id;
    LocalDateTime start;
    LocalDateTime end;
    Long bookerId;
    Status status;
}
