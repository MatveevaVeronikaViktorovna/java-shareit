package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
@RequiredArgsConstructor
@Slf4j
public abstract class ItemRepositoryInMemoryImpl implements ItemRepository {
/*

    @Override
    public Item update(Long userId, Long id, Item item) {
        Item oldItem = items.get(id);
        if (isThisOwnersItem(userId, id)) {
            item.setId(id);
            if (item.getName() == null) {
                item.setName(oldItem.getName());
            }
            if (item.getDescription() == null) {
                item.setDescription(oldItem.getDescription());
            }
            if (item.getAvailable() == null) {
                item.setAvailable(oldItem.getAvailable());
            }
            items.put(id, item);
            return item;
        } else {
            log.warn("Вещь с id " + id + " не найдена у владельца с id " + userId);
            throw new EntityNotFoundException(Item.class.getSimpleName(), id);
        }
    }


    @Override
    public List<Item> findAvailableByText(Long userId, String text) {
        List<Item> searchResults = new ArrayList<>();
        for (Item item : items.values()) {
            String name = item.getName().toLowerCase();
            String description = item.getDescription().toLowerCase();
            if (item.getAvailable() && (name.contains(text.toLowerCase()) || description.contains(text.toLowerCase()))) {
                searchResults.add(item);
            }
        }
        return searchResults;
    }

*/
}
