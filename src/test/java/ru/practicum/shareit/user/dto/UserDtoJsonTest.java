package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoJsonTest {

    @Autowired
    private JacksonTester<UserDto> json;
    private final UserDto userDto = new UserDto();

    @Test
    void testUserDtoId() throws Exception {
        userDto.setId(1L);

        JsonContent<UserDto> result = json.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
    }

    @Test
    void testUserDtoName() throws Exception {
        userDto.setName("name");

        JsonContent<UserDto> result = json.write(userDto);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("name");
    }

    @Test
    void testUserDtoEmail() throws Exception {
        userDto.setEmail("name@yandex.ru");

        JsonContent<UserDto> result = json.write(userDto);

        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("name@yandex.ru");
    }

}