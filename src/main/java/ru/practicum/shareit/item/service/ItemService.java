package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDtoForCreate;
import ru.practicum.shareit.item.dto.ItemDtoForUpdate;

import java.util.List;

public interface ItemService {

    ItemDtoForCreate create(Long userId, ItemDtoForCreate itemDtoForCreate);

    List<ItemDtoForCreate> getAllByOwner(Long userId);

    ItemDtoForCreate getById(Long id);

    ItemDtoForCreate update(Long userId, Long id, ItemDtoForUpdate itemDtoForUpdate);

    void delete(Long id);

    List<ItemDtoForCreate> findAvailableByText(Long userId, String text);
}
