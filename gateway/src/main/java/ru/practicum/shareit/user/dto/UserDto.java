package ru.practicum.shareit.user.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class UserDto {
    Long id;
    @Size(max = 128)
    @NotBlank(groups = Create.class)
    String name;
    @Size(max = 128)
    @NotBlank(groups = Create.class)
    @Email(groups = {Create.class, Update.class})
    String email;
}
