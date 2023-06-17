package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.dto.Create;
import valid.StartBeforeEndDateValid;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@StartBeforeEndDateValid(groups = Create.class)
public class BookingDto {
    @FutureOrPresent(groups = Create.class)
    LocalDateTime start;
    @Future(groups = Create.class)
    LocalDateTime end;
    @NotNull(groups = Create.class)
    Long itemId;
}
