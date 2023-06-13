package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForResponse;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingEndBeforeStartException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingDtoForResponse create(Long userId, BookingDto bookingDto) {
        Booking booking = BookingMapper.toBooking(bookingDto);

        if(booking.getEnd().isBefore(booking.getStart())) {
            log.warn("Время окончания бронирования раньше чем время начала бронирования");
            throw new BookingEndBeforeStartException("Время окончания бронирования раньше чем время начала бронирования");
        }

        if(booking.getEnd().equals(booking.getStart())) {
            log.warn("Время окончания бронирования совпадает со временем начала бронирования");
            throw new BookingEndBeforeStartException("Время окончания бронирования совпадает со временем начала бронирования");
        }

        Optional<Item> item = itemRepository.findById(bookingDto.getItemId());
        if(item.isPresent()) {
            booking.setItem(item.get());
        } else {
            log.warn("Вещь с id " + bookingDto.getItemId() + " не найдена");
            throw new EntityNotFoundException(Item.class.getSimpleName(), bookingDto.getItemId());
        }

        if (!item.get().getAvailable()) {
            log.warn("Вещь с id " + bookingDto.getItemId() + " недоступна для бронирования");
            throw new ItemNotAvailableException("Вещь с id " + bookingDto.getItemId() + " недоступна для бронирования");
        }

        Optional<User> booker = userRepository.findById(userId);
        if (booker.isPresent()) {
            booking.setBooker(booker.get());
        } else {
            log.warn("Пользователь с id " + userId + " не найден");
            throw new EntityNotFoundException(User.class.getSimpleName(), userId);
        }
        booking.setStatus(Status.WAITING);
        Booking newBooking = bookingRepository.save(booking);
        log.info("Добавлено бронирование: {}", newBooking);
        return BookingMapper.toDto(newBooking);
    }

}
