package ru.practicum.shareit.user.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class UserDtoForUpdate {
    long id;
    @Email
    String email;
    String name;
}
