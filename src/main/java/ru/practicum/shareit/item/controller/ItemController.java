package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.Update;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.Create;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @Validated(Create.class) @RequestBody ItemDto itemDto) {
        return itemService.create(userId, itemDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> getAllByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getAllByOwner(userId);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto getById(@PathVariable Long id) {
        return itemService.getById(id);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @PathVariable Long id,
                          @Validated(Update.class) @RequestBody ItemDto itemDto) {
        return itemService.update(userId, id, itemDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable Long id) {
        itemService.delete(id);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> findAvailableByText(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam String text) {
        return itemService.findAvailableByText(userId, text);
    }

}
