package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.controller.State;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForResponse;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingAlreadyApprovedException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

        Optional<Item> item = itemRepository.findById(bookingDto.getItemId());
        if (item.isPresent()) {
            if (!item.get().getAvailable()) {
                log.warn("Вещь с id {} недоступна для бронирования", bookingDto.getItemId());
                throw new ItemNotAvailableException("Вещь с id " + bookingDto.getItemId() + " недоступна для " +
                        "бронирования");
            }
            booking.setItem(item.get());
        } else {
            log.warn("Вещь с id {} не найдена", bookingDto.getItemId());
            throw new EntityNotFoundException("Вещь с id " + bookingDto.getItemId() + " не найдена");
        }

        Optional<User> booker = userRepository.findById(userId);
        if (booker.isPresent()) {
            if (booking.getItem().getOwner().getId().equals(userId)) {
                log.warn("Пользователь с id {} является владельцем вещи с id {} и не может ее забронировать",
                        userId, bookingDto.getItemId());
                throw new EntityNotFoundException("Пользователь с id " + userId + " является владельцем вещи с id "
                        + bookingDto.getItemId() + " и не может ее забронировать");
            }
            booking.setBooker(booker.get());
        } else {
            log.warn("Пользователь с id {} не найден", userId);
            throw new EntityNotFoundException("Пользователь с id " + userId + " не найден");
        }

        booking.setStatus(Status.WAITING);
        Booking newBooking = bookingRepository.save(booking);
        log.info("Добавлено бронирование: {}", newBooking);
        return BookingMapper.toDto(newBooking);
    }

    @Override
    public BookingDtoForResponse approveOrReject(Long userId, Long id, Boolean approved) {
        Optional<Booking> bookingOptional = bookingRepository.findByIdAndItemOwnerId(id, userId);
        if (bookingOptional.isPresent()) {
            Booking booking = bookingOptional.get();
            if (approved) {
                if (booking.getStatus() == Status.APPROVED) {
                    log.warn("Бронирование с id {} уже подтверждено пользователем с id {}", id, userId);
                    throw new BookingAlreadyApprovedException("Бронирование с id " + id + " уже подтверждено " +
                            "пользователем с id " + userId);
                }
                booking.setStatus(Status.APPROVED);
            } else {
                booking.setStatus(Status.REJECTED);
            }
            Booking newBooking = bookingRepository.save(booking);
            return BookingMapper.toDto(newBooking);
        } else {
            log.warn("Бронирование с id {} у пользователя с id {} не найдено", id, userId);
            throw new EntityNotFoundException("Бронирование с id " + id + " у пользователя с id "
                    + userId + " не найдено");
        }
    }

    @Override
    public BookingDtoForResponse getById(Long userId, Long id) {
        Optional<Booking> booking = bookingRepository.findById(id);
        if (booking.isPresent()) {
            if (booking.get().getBooker().getId().equals(userId) ||
                    booking.get().getItem().getOwner().getId().equals(userId)) {
                return BookingMapper.toDto(booking.get());
            } else {
                log.warn("Бронирование с id {} у пользователя с id {} не найдено", id, userId);
                throw new EntityNotFoundException("Бронирование с id " + id + " у пользователя с id "
                        + userId + " не найдено");
            }
        } else {
            log.warn("Бронирование с id {} не найдено", id);
            throw new EntityNotFoundException("Бронирование с id " + id + " не найдено");
        }
    }

    @Override
    public List<BookingDtoForResponse> getAllByBooker(Long userId, State state) {
        if (!userRepository.existsById(userId)) {
            log.warn("Пользователь с id {} не найден", userId);
            throw new EntityNotFoundException("Пользователь с id " + userId + " не найден");
        }
        List<Booking> bookings = new ArrayList<>();
        LocalDateTime currentMoment = LocalDateTime.now();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                        currentMoment, currentMoment);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, currentMoment);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, currentMoment);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
        }
        List<BookingDtoForResponse> bookingsForResponse = new ArrayList<>();
        for (Booking booking : bookings) {
            bookingsForResponse.add(BookingMapper.toDto(booking));
        }
        return bookingsForResponse;
    }

    @Override
    public List<BookingDtoForResponse> getAllByOwner(Long userId, State state) {
        if (!userRepository.existsById(userId)) {
            log.warn("Пользователь с id {} не найден", userId);
            throw new EntityNotFoundException("Пользователь с id " + userId + " не найден");
        }
        List<Booking> bookings = new ArrayList<>();
        LocalDateTime currentMoment = LocalDateTime.now();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                        currentMoment, currentMoment);
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, currentMoment);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId, currentMoment);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
        }
        List<BookingDtoForResponse> bookingsForResponse = new ArrayList<>();
        for (Booking booking : bookings) {
            bookingsForResponse.add(BookingMapper.toDto(booking));
        }
        return bookingsForResponse;
    }

}
