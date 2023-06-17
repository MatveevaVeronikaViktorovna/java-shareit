package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.dto.Create;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class BookingDto {
    @NotNull(groups = Create.class)
    @FutureOrPresent(groups = Create.class)
    LocalDateTime start;
    @Future(groups = Create.class)
    @NotNull(groups = Create.class)
    LocalDateTime end;
    @NotNull(groups = Create.class)
    Long itemId;
}
