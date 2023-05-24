package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    Item create(Item item);

    List<Item> getAllByOwner(Long userId);

    Item getById(Long id);

    Item update(Long userId, Long id, Item item);

    void delete(Long id);

    List<Item> findAvailableByText(Long userId, String text);
}
