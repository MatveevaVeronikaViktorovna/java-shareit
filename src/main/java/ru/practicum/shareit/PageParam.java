package ru.practicum.shareit;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class PageParam {
    @PositiveOrZero
    Integer from;
    @Positive
    Integer size;
}
