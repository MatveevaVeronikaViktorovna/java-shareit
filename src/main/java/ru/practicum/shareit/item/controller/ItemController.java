package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.Update;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.Create;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.shareit.booking.controller.BookingController.HEADER;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestHeader(HEADER) Long userId,
                          @Validated(Create.class) @RequestBody ItemDto itemDto) {
        return itemService.create(userId, itemDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> getAllByOwner(@RequestHeader(HEADER) Long userId,
                                       @RequestParam(defaultValue = "0") @Valid @PositiveOrZero Integer from,
                                       @RequestParam(defaultValue = "20") @Valid @Positive Integer size) {
        return itemService.getAllByOwner(userId, from, size);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto getById(@RequestHeader(HEADER) Long userId,
                           @PathVariable Long id) {
        return itemService.getById(userId, id);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto update(@RequestHeader(HEADER) Long userId,
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
    public List<ItemDto> searchByText(@RequestHeader(HEADER) Long userId,
                                      @RequestParam String text,
                                      @RequestParam(defaultValue = "0") @Valid @PositiveOrZero Integer from,
                                      @RequestParam(defaultValue = "20") @Valid @Positive Integer size) {
        return itemService.searchByText(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto addComment(@RequestHeader(HEADER) Long userId,
                                 @PathVariable Long itemId,
                                 @Validated(Create.class) @RequestBody CommentDto commentDto) {
        return itemService.addComment(userId, itemId, commentDto);
    }

}
