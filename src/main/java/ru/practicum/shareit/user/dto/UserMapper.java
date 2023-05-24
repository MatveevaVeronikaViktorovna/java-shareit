package ru.practicum.shareit.user.dto;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

@Data
public class UserMapper {

    public static User convertDtoForCreateToUser(UserDtoForCreate dto) {
        if (dto == null) return null;
        else {
            User user = new User();
            user.setName(dto.getName());
            user.setEmail(dto.getEmail());
            return user;
        }
    }

    public static User convertDtoForUpdateToUser(UserDtoForUpdate dto) {
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

    public static UserDtoForCreate convertUserToDto(User user) {
        UserDtoForCreate userDtoForCreate = new UserDtoForCreate();
        userDtoForCreate.setId(user.getId());
        userDtoForCreate.setName(user.getName());
        userDtoForCreate.setEmail(user.getEmail());
        return userDtoForCreate;
    }

}
