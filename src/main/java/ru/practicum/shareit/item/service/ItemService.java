package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto create(Long userId, ItemDto itemDto);

    List<ItemDto> getAllByOwner(Long userId, Integer from, Integer size);

    ItemDto getById(Long userId, Long id);

    ItemDto update(Long userId, Long id, ItemDto itemDto);

    void delete(Long id);

    List<ItemDto> searchByText(Long userId, String text, Integer from, Integer size);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);
}
