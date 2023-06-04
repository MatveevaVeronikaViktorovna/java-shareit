package ru.practicum.shareit.user.dto;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

@Data
public class UserMapper {

    public static User toUser(UserDto dto) {
        if (dto == null) return null;
        else {
            User user = new User();
            if (dto.getName() != null) {
                user.setName(dto.getName());
            }
            if (dto.getEmail() != null) {
                user.setEmail(dto.getEmail());
            }
            return user;
        }
    }

    public static UserDto toDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }

}
