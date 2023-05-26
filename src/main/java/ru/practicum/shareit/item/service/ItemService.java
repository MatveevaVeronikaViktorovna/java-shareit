package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto create(Long userId, ItemDto itemDto);

    List<ItemDto> getAllByOwner(Long userId);

    ItemDto getById(Long id);

    ItemDto update(Long userId, Long id, ItemDto itemDto);

    void delete(Long id);

    List<ItemDto> findAvailableByText(Long userId, String text);
}
