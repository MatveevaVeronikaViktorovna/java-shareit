package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.CommentWithoutBookingException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        Optional<User> owner = userRepository.findById(userId);
        if (owner.isPresent()) {
            item.setOwner(owner.get());
        } else {
            log.warn("Пользователь с id {} не найден", userId);
            throw new EntityNotFoundException(String.format("Пользователь с id %d не найден", userId));
        }
        Item newItem = itemRepository.save(item);
        log.info("Добавленa вещь: {}", newItem);
        return ItemMapper.toDto(newItem);
    }

    @Override
    public List<ItemDto> getAllByOwner(Long userId) {
        List<Item> thisOwnerItems = itemRepository.findAllByOwnerIdOrderByIdAsc(userId);
        List<ItemDto> items = new ArrayList<>();
        for (Item item : thisOwnerItems) {
            ItemDto itemDto = setLastBookingAndNextBooking(item);
            setComments(itemDto);
            items.add(itemDto);
        }
        return items;
    }

    @Override
    public ItemDto getById(Long userId, Long id) {
        Optional<Item> itemOptional = itemRepository.findById(id);
        if (itemOptional.isPresent()) {
            Item item = itemOptional.get();
            ItemDto itemDto;
            if (item.getOwner().getId().equals(userId)) {
                itemDto = setLastBookingAndNextBooking(itemOptional.get());
            } else {
                itemDto = ItemMapper.toDto(item);
            }
            setComments(itemDto);
            return itemDto;
        } else {
            log.warn("Вещь с id {} не найдена", id);
            throw new EntityNotFoundException(String.format("Вещь с id %d не найдена", id));
        }
    }

    @Override
    public ItemDto update(Long userId, Long id, ItemDto itemDto) {
        Item newItem = ItemMapper.toItem(itemDto);
        Optional<Item> oldItemOptional = itemRepository.findById(id);
        Item oldItem;
        if (oldItemOptional.isPresent()) {
            oldItem = oldItemOptional.get();
        } else {
            log.warn("Вещь с id {} не найдена", id);
            throw new EntityNotFoundException(String.format("Вещь с id %d не найдена", id));
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
            log.warn("Вещь с id {} не найдена у владельца с id {}",id, userId);
            throw new EntityNotFoundException(String.format("Вещь с id %d не найдена у владельца с id %d",id, userId));
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
    public List<ItemDto> searchByText(Long userId, String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        } else {
            List<Item> searchResults = itemRepository.findAllAvailableByText(text);
            List<ItemDto> items = new ArrayList<>();
            for (Item item : searchResults) {
                items.add(ItemMapper.toDto(item));
            }
            return items;
        }
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndItemIdAndEndBeforeOrderByStartDesc(userId,
                itemId, LocalDateTime.now());
        if (bookings.isEmpty()) {
            log.warn("Отзыв может оставить только тот пользователь, который брал эту вещь в аренду, и только после " +
                    "окончания срока аренды.");
            throw new CommentWithoutBookingException("Отзыв может оставить только тот пользователь, который брал эту " +
                    "вещь в аренду, и только после окончания срока аренды.");
        }
        Comment comment = CommentMapper.toComment(commentDto);
        Optional<User> author = userRepository.findById(userId);
        if (author.isPresent()) {
            comment.setAuthor(author.get());
        } else {
            log.warn("Пользователь с id {} не найден", userId);
            throw new EntityNotFoundException(String.format("Пользователь с id %d не найден", userId));
        }
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isPresent()) {
            comment.setItem(item.get());
        } else {
            log.warn("Вещь с id {} не найдена", itemId);
            throw new EntityNotFoundException(String.format("Вещь с id %d не найдена", itemId));
        }
        comment.setCreated(LocalDateTime.now());
        Comment newComment = commentRepository.save(comment);
        log.info("Добавлен комментарий: {}", newComment);
        return CommentMapper.toDto(newComment);
    }

    private boolean isThisOwnersItem(Long userId, Long id) {
        Item item = itemRepository.findById(id).get();
        User owner = item.getOwner();
        return Objects.equals(owner.getId(), userId);
    }

    private ItemDto setLastBookingAndNextBooking(Item item) {
        ItemDto itemDto = ItemMapper.toDto(item);
        LocalDateTime currentMoment = LocalDateTime.now();
        Optional<Booking> lastBooking = bookingRepository.findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(item.getId(), currentMoment, Status.APPROVED);
        lastBooking.ifPresent(booking -> itemDto.setLastBooking(BookingMapper.toDtoForItem(lastBooking.get())));
        Optional<Booking> nextBooking = bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(item.getId(), currentMoment, Status.APPROVED);
        nextBooking.ifPresent(booking -> itemDto.setNextBooking(BookingMapper.toDtoForItem(nextBooking.get())));
        return itemDto;
    }

    private void setComments(ItemDto itemDto) {
        List<Comment> comments = commentRepository.findAllByItemId(itemDto.getId());
        List<CommentDto> commentsDto = new ArrayList<>();
        for (Comment comment : comments) {
            commentsDto.add(CommentMapper.toDto(comment));
        }
        itemDto.setComments(commentsDto);
    }

}
