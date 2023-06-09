package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        Optional<User> owner = userRepository.findById(userId);
        if(owner.isPresent()) {
            item.setOwner(owner.get());
        } else {
            log.warn("Пользователь с id " + userId + " не найден");
            throw new EntityNotFoundException(User.class.getSimpleName(), userId);
        }
        Item newItem = itemRepository.save(item);
        log.info("Добавленa вещь: {}", newItem);
        return ItemMapper.toDto(newItem);
    }

    @Override
    public List<ItemDto> getAllByOwner(Long userId) {
        List<Item> thisOwnerItems = itemRepository.findAllByOwnerId(userId);
        List<ItemDto> items = new ArrayList<>();
        for (Item item : thisOwnerItems) {
            items.add(ItemMapper.toDto(item));
        }
        return items;
    }

    @Override
    public ItemDto getById(Long id) {
        Optional<Item> item = itemRepository.findById(id);
        if(item.isPresent()) {
            return ItemMapper.toDto(item.get());
        } else {
            log.warn("Вещь с id " + id + " не найдена");
            throw new EntityNotFoundException(Item.class.getSimpleName(), id);
        }
    }

    @Override
    public ItemDto update(Long userId, Long id, ItemDto itemDto) {
        Item newItem = ItemMapper.toItem(itemDto);
        Optional<Item> oldItemOptional = itemRepository.findById(id);
        Item oldItem;
        if(oldItemOptional.isPresent()) {
            oldItem = oldItemOptional.get();
        } else {
            log.warn("Вещь с id " + id + " не найдена");
            throw new EntityNotFoundException(Item.class.getSimpleName(), id);
        }
        if (isThisOwnersItem(userId, id)) {
            newItem.setId(id);
            if (newItem.getName() == null) {
                newItem.setName(oldItem.getName());
            }
            if (newItem.getDescription() == null) {
                newItem.setDescription(oldItem.getDescription());
            }
            if (newItem.getAvailable() == null) {
                newItem.setAvailable(oldItem.getAvailable());
            }
            newItem.setOwner(oldItem.getOwner());
        } else {
            log.warn("Вещь с id " + id + " не найдена у владельца с id " + userId);
            throw new EntityNotFoundException(Item.class.getSimpleName(), id);
        }
        Item updatedItem = itemRepository.save(newItem);
        log.info("Обновлена вещь c id {} на {}", id, updatedItem);
        return ItemMapper.toDto(updatedItem);
    }

    @Override
    public void delete(Long id) {
        itemRepository.deleteById(id);
        log.info("Удалена вещь с id {}", id);
    }

    @Override
    public List<ItemDto> findAvailableByText(Long userId, String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        } else {
            List<Item> searchResults = itemRepository.findAllByNameContainingIgnoreCase(text);
            System.out.println("нашли: " + searchResults);
            List<ItemDto> items = new ArrayList<>();
            for (Item item : searchResults) {
                items.add(ItemMapper.toDto(item));
            }
            return items;
        }
    }

    private boolean isThisOwnersItem(Long userId, Long id) {
        Item item = itemRepository.findById(id).get();
        User owner = item.getOwner();
        return Objects.equals(owner.getId(), userId);
    }

}
