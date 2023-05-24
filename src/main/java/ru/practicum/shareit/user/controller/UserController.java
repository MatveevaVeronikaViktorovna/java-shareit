package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDtoForCreate;
import ru.practicum.shareit.user.dto.UserDtoForUpdate;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDtoForCreate create(@Valid @RequestBody UserDtoForCreate userDtoForCreate) {
        return userService.create(userDtoForCreate);
    }

    @GetMapping
    public List<UserDtoForCreate> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public UserDtoForCreate getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @PatchMapping("/{id}")
    public UserDtoForCreate update(@PathVariable Long id,
                                   @Valid @RequestBody UserDtoForUpdate userDtoForUpdate) {
        return userService.update(id, userDtoForUpdate);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }

}
