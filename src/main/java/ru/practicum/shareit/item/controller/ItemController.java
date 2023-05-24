package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
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
    public ItemDtoForCreate create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                   @Valid @RequestBody ItemDtoForCreate itemDtoForCreate) {
        return itemService.create(userId, itemDtoForCreate);
    }

    @GetMapping
    public List<ItemDtoForCreate> getAllByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getAllByOwner(userId);
    }

    @GetMapping("/{id}")
    public ItemDtoForCreate getById(@PathVariable Long id) {
        return itemService.getById(id);
    }

    @PatchMapping("/{id}")
    public ItemDtoForCreate update(@RequestHeader("X-Sharer-User-Id") Long userId,
                                   @PathVariable Long id,
                                   @Valid @RequestBody ItemDtoForUpdate itemDtoForUpdate) {
        return itemService.update(userId, id, itemDtoForUpdate);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        itemService.delete(id);
    }

    @GetMapping("/search")
    public List<ItemDtoForCreate> findAvailableByText(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @RequestParam String text) {
        return itemService.findAvailableByText(userId, text);
    }

}
