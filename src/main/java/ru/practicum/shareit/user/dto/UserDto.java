package ru.practicum.shareit.user.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class UserDto {
    long id;
    @NotBlank(groups = Create.class)
    @Email(groups = {Create.class, Update.class})
    String email;
    @NotBlank(groups = Create.class)
    String name;
}