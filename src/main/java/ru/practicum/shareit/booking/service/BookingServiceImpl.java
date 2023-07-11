package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import ru.practicum.shareit.pagination.CustomPageRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public BookingDtoForResponse create(Long userId, BookingDto bookingDto) {
        Booking booking = BookingMapper.toBooking(bookingDto);

        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> {
            log.warn("Вещь с id {} не найдена", bookingDto.getItemId());
            throw new EntityNotFoundException(String.format("Вещь с id %d не найдена", bookingDto.getItemId()));
        });
        if (!item.getAvailable()) {
            log.warn("Вещь с id {} недоступна для бронирования", bookingDto.getItemId());
            throw new ItemNotAvailableException(String.format("Вещь с id %d недоступна для бронирования",
                    bookingDto.getItemId()));
        }
        booking.setItem(item);

        User booker = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Пользователь с id {} не найден", userId);
            throw new EntityNotFoundException(String.format("Пользователь с id %d не найден", userId));
        });
        if (booking.getItem().getOwner().getId().equals(userId)) {
            log.warn("Пользователь с id {} является владельцем вещи с id {} и не может ее забронировать", userId,
                    bookingDto.getItemId());
            throw new EntityNotFoundException(String.format("Пользователь с id %d является владельцем вещи с id %d " +
                    "и не может ее забронировать", userId, bookingDto.getItemId()));
        }
        booking.setBooker(booker);

        booking.setStatus(Status.WAITING);
        Booking newBooking = bookingRepository.save(booking);
        log.info("Добавлено бронирование: {}", newBooking);
        return BookingMapper.toDto(newBooking);
    }

    @Transactional
    @Override
    public BookingDtoForResponse approveOrReject(Long userId, Long id, Boolean approved) {
        Booking booking = bookingRepository.findByIdAndItemOwnerId(id, userId).orElseThrow(() -> {
            log.warn("Бронирование с id {} у пользователя с id {} не найдено", id, userId);
            throw new EntityNotFoundException(String.format("Бронирование с id %d у пользователя с id %d не найдено",
                    id, userId));
        });

        if (approved) {
            if (booking.getStatus() == Status.APPROVED) {
                log.warn("Бронирование с id {} уже подтверждено пользователем с id {}", id, userId);
                throw new BookingAlreadyApprovedException(String.format("Бронирование с id %d уже подтверждено " +
                        "пользователем с id %d", id, userId));
            }
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }

        Booking newBooking = bookingRepository.save(booking);
        return BookingMapper.toDto(newBooking);
    }

    @Transactional(readOnly = true)
    @Override
    public BookingDtoForResponse getById(Long userId, Long id) {
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> {
            log.warn("Бронирование с id {} не найдено", id);
            throw new EntityNotFoundException(String.format("Бронирование с id %d не найдено", id));
        });

        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId)) {
            return BookingMapper.toDto(booking);
        } else {
            log.warn("Бронирование с id {} у пользователя с id {} не найдено", id, userId);
            throw new EntityNotFoundException(String.format("Бронирование с id %d у пользователя с id %d не найдено",
                    id, userId));
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDtoForResponse> getAllByBooker(Long userId, State state, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            log.warn("Пользователь с id {} не найден", userId);
            throw new EntityNotFoundException(String.format("Пользователь с id %d не найден", userId));
        }
        Pageable page = CustomPageRequest.of(from, size);
        List<Booking> bookings = new ArrayList<>();
        LocalDateTime currentMoment = LocalDateTime.now();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId, page);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                        currentMoment, currentMoment, page);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, currentMoment, page);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, currentMoment,
                        page);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING, page);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED, page);
        }
        return bookings
                .stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDtoForResponse> getAllByOwner(Long userId, State state, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            log.warn("Пользователь с id {} не найден", userId);
            throw new EntityNotFoundException(String.format("Пользователь с id %d не найден", userId));
        }
        Pageable page = CustomPageRequest.of(from, size);
        List<Booking> bookings = new ArrayList<>();
        LocalDateTime currentMoment = LocalDateTime.now();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId, page);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                        currentMoment, currentMoment, page);
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, currentMoment,
                        page);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId, currentMoment,
                        page);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.WAITING,
                        page);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.REJECTED,
                        page);
        }
        return bookings
                .stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }

}
