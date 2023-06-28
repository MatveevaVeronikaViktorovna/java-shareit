package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForResponse;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Captor
    private ArgumentCaptor<Booking> bookingArgumentCaptor;

    @Test
    void createWhenItemFoundAndAvailableAndBookerFoundAndNotOwnerThenSavedBooking() {
        Long userId = 0L;
        User booker = new User();
        booker.setId(userId);

        Long itemId = 0L;
        Item item = new Item();
        item.setId(itemId);
        item.setAvailable(true);
        User owner = new User();
        owner.setId(1L);
        item.setOwner(owner);

        BookingDto requestBookingDto = new BookingDto();
        requestBookingDto.setItemId(itemId);

        Booking expectedBooking = new Booking();
        expectedBooking.setBooker(booker);
        expectedBooking.setItem(item);
        BookingDtoForResponse expectedBookingDto = BookingMapper.toDto(expectedBooking);

        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booker));
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class))).thenReturn(expectedBooking);

        BookingDtoForResponse booking = bookingService.create(userId, requestBookingDto);

        assertEquals(expectedBookingDto, booking);
        verify(bookingRepository).save(Mockito.any(Booking.class));
    }


    @Test
    void createWhenItemNotFoundThenNotSavedBooking() {
        Long userId = 0L;
        Long itemId = 0L;
        BookingDto requestBookingDto = new BookingDto();
        requestBookingDto.setItemId(itemId);
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.create(userId,requestBookingDto));

        verify(bookingRepository, Mockito.never()).save(Mockito.any(Booking.class));
    }

    @Test
    void createWhenUserNotFoundThenNotSavedBooking() {
        Long userId = 0L;

        Long itemId = 0L;
        Item item = new Item();
        item.setAvailable(true);

        BookingDto requestBookingDto = new BookingDto();
        requestBookingDto.setItemId(itemId);
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.create(userId,requestBookingDto));

        verify(bookingRepository, Mockito.never()).save(Mockito.any(Booking.class));
    }

    @Test
    void createWhenItemFoundButNotAvailableAndBookerFoundAndNotOwnerThenNotSavedBooking() {
        Long userId = 0L;

        Long itemId = 0L;
        Item item = new Item();
        item.setAvailable(false);

        BookingDto requestBookingDto = new BookingDto();
        requestBookingDto.setItemId(itemId);

        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));

        assertThrows(ItemNotAvailableException.class, () -> bookingService.create(userId,requestBookingDto));

        verify(bookingRepository, Mockito.never()).save(Mockito.any(Booking.class));
    }

    @Test
    void createWhenItemFoundAndAvailableAndBookerFoundButEqualOwnerThenNotSavedBooking() {
        Long userId = 0L;
        User booker = new User();
        booker.setId(userId);

        Long itemId = 0L;
        Item item = new Item();
        item.setAvailable(true);
        User owner = new User();
        owner.setId(userId);
        item.setOwner(owner);

        BookingDto requestBookingDto = new BookingDto();
        requestBookingDto.setItemId(itemId);
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booker));

        assertThrows(EntityNotFoundException.class, () -> bookingService.create(userId,requestBookingDto));

        verify(bookingRepository, Mockito.never()).save(Mockito.any(Booking.class));
    }



}