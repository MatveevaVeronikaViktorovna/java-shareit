package ru.practicum.shareit.user.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class UserMapper {

    public User toUser(UserDto dto) {
        if (dto == null) return null;
        else {
            User user = new User();
            user.setName(dto.getName());
            user.setEmail(dto.getEmail());
            return user;
        }
    }

    public UserDto toDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    public List<UserDto> toDto(List<User> users) {
        List<UserDto> result = new ArrayList<>();

        for (User user : users) {
            result.add(toDto(user));
        }

        return result;
    }

}
