package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDtoForCreate;
import ru.practicum.shareit.item.dto.ItemDtoForUpdate;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDtoForCreate create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                   @Valid @RequestBody ItemDtoForCreate itemDtoForCreate) {
        return itemService.create(userId, itemDtoForCreate);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDtoForCreate> getAllByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getAllByOwner(userId);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDtoForCreate getById(@PathVariable Long id) {
        return itemService.getById(id);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDtoForCreate update(@RequestHeader("X-Sharer-User-Id") Long userId,
                                   @PathVariable Long id,
                                   @Valid @RequestBody ItemDtoForUpdate itemDtoForUpdate) {
        return itemService.update(userId, id, itemDtoForUpdate);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        itemService.delete(id);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDtoForCreate> findAvailableByText(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @RequestParam String text) {
        return itemService.findAvailableByText(userId, text);
    }

}
