package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.Create;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class BookingDto {
    Long id;
    @NotNull(groups = Create.class)
    @FutureOrPresent(groups = Create.class)
    LocalDateTime start;
    @Future(groups = Create.class)
    @NotNull(groups = Create.class)
    LocalDateTime end;
    UserDto booker;
    @NotNull(groups = Create.class)
    Long itemId;
    ItemDto item; //это убрать или мы где то используем их? уберу чуть позже
    Status status; // это убрать
}
