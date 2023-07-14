package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static ru.practicum.shareit.booking.controller.BookingController.HEADER;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestHeader(HEADER) Long userId,
                          @RequestBody ItemDto itemDto) {
        log.info("Поступил запрос на создание вещи {} от пользователя с id={}", itemDto, userId);
        return itemService.create(userId, itemDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> getAllByOwner(@RequestHeader(HEADER) Long userId,
                                       @RequestParam(defaultValue = "0") Integer from,
                                       @RequestParam(defaultValue = "20") Integer size) {
        log.info("Поступил запрос на получение всех вещей от пользователя с id={}, from={}, size={}",
                userId, from, size);
        return itemService.getAllByOwner(userId, from, size);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto getById(@RequestHeader(HEADER) Long userId,
                           @PathVariable Long id) {
        log.info("Поступил запрос на получение вещи с id={} от пользователя с id={}", id, userId);
        return itemService.getById(userId, id);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto update(@RequestHeader(HEADER) Long userId,
                          @PathVariable Long id,
                          @RequestBody ItemDto itemDto) {
        log.info("Поступил запрос на обновление вещи с id={} от пользователя с id={} на {}", id, userId, itemDto);
        return itemService.update(userId, id, itemDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable Long id) {
        log.info("Поступил запрос на удаление вещи с id={}", id);
        itemService.delete(id);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> searchByText(@RequestHeader(HEADER) Long userId,
                                      @RequestParam String text,
                                      @RequestParam(defaultValue = "0") Integer from,
                                      @RequestParam(defaultValue = "20") Integer size) {
        log.info("Поступил запрос на поиск всех вещей по тексту от пользователя с id={}, text={}, from={}, size={}",
                userId, text, from, size);
        return itemService.searchByText(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto addComment(@RequestHeader(HEADER) Long userId,
                                 @PathVariable Long itemId,
                                 @RequestBody CommentDto commentDto) {
        log.info("Поступил запрос на добавление комментария {} от пользователя с id={} к вещи с id={}",
                commentDto, userId, itemId);
        return itemService.addComment(userId, itemId, commentDto);
    }

}
