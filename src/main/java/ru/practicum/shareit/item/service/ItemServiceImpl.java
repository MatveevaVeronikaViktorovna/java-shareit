package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDtoForCreate;
import ru.practicum.shareit.item.dto.ItemDtoForUpdate;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDtoForCreate create(Long userId, ItemDtoForCreate itemDtoForCreate) {
        Item item = ItemMapper.convertDtoForCreateToItem(itemDtoForCreate);
        User owner = userRepository.getById(userId);
        item.setOwner(owner);
        Item newItem = itemRepository.create(item);
        log.info("Добавленa вещь: {}", newItem);
        return ItemMapper.convertItemToDto(newItem);
    }

    @Override
    public List<ItemDtoForCreate> getAllByOwner(Long userId) {
        List<Item> thisOwnerItems = itemRepository.getAllByOwner(userId);
        List<ItemDtoForCreate> items = new ArrayList<>();
        for (Item item : thisOwnerItems) {
            items.add(ItemMapper.convertItemToDto(item));
        }
        return items;
    }

    @Override
    public ItemDtoForCreate getById(Long id) {
        Item item = itemRepository.getById(id);
        return ItemMapper.convertItemToDto(item);
    }

    @Override
    public ItemDtoForCreate update(Long userId, Long id, ItemDtoForUpdate itemDtoForUpdate) {
        Item newItem = ItemMapper.convertDtoForUpdateToItem(itemDtoForUpdate);
        User owner = userRepository.getById(userId);
        Item updatedItem = itemRepository.update(userId, id, newItem);
        updatedItem.setOwner(owner);
        log.info("Обновлена вещь c id {} на {}", id, updatedItem);
        return ItemMapper.convertItemToDto(updatedItem);
    }

    @Override
    public void delete(Long id) {
        itemRepository.delete(id);
        log.info("Удалена вещь с id {}", id);
    }

    @Override
    public List<ItemDtoForCreate> findAvailableByText(Long userId, String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        } else {
            List<Item> searchResults = itemRepository.findAvailableByText(userId, text);
            List<ItemDtoForCreate> items = new ArrayList<>();
            for (Item item : searchResults) {
                items.add(ItemMapper.convertItemToDto(item));
            }
            return items;
        }
    }

}
