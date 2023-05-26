package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ItemRepositoryInMemoryImpl implements ItemRepository {

    private final Map<Long, Item> items = new HashMap<>();
    private long newId;

    @Override
    public Item create(Item item) {
        newId++;
        item.setId(newId);
        items.put(newId, item);
        return item;
    }

    @Override
    public List<Item> getAllByOwner(Long userId) {
        List<Item> thisOwnerItems = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner().getId() == userId) {
                thisOwnerItems.add(item);
            }
        }
        return thisOwnerItems;
    }

    @Override
    public Item getById(Long id) {
        if (items.containsKey(id)) {
            return items.get(id);
        } else {
            log.warn("Вещь с id " + id + " не найдена");
            throw new ItemNotFoundException("Вещь с id " + id + " не найдена");
        }
    }

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
            throw new ItemNotFoundException("Вещь с id " + id + " не найдена у владельца с id " + userId);
        }
    }

    @Override
    public void delete(Long id) {
        items.remove(id);
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

    private boolean isThisOwnersItem(Long userId, Long id) {
        Item item = items.get(id);
        User owner = item.getOwner();
        return owner.getId() == userId;
    }

}
