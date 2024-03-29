package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.Create;
import ru.practicum.shareit.user.dto.Update;
import ru.practicum.shareit.user.dto.UserDto;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Slf4j
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(@Validated(Create.class) @RequestBody UserDto requestDto) {
        log.info("Поступил запрос на создание пользователя {}", requestDto);
        return userClient.create(requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("Поступил запрос на получение всех пользователей");
        return userClient.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable Long id) {
        log.info("Поступил запрос на получение пользователя с id={}", id);
        return userClient.getById(id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id,
                                         @Validated(Update.class) @RequestBody UserDto requestDto) {
        log.info("Поступил запрос на обновление пользователя с id={} на {}", id, requestDto);
        return userClient.update(id, requestDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        log.info("Поступил запрос на удаление пользователя с id={}", id);
        return userClient.delete(id);
    }

}
