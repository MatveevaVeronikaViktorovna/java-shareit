package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
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

    @Test
    void approveWhenBookingFoundAndApprovedIsTrueThenUpdatedBooking(){
        Boolean approved = true;

        Long userId = 0L;
        User booker = new User();
        booker.setId(userId);

        Long itemId = 0L;
        Item item = new Item();
        item.setId(itemId);

        Long id = 0L;
        Booking oldBooking = new Booking();
        oldBooking.setId(id);
        oldBooking.setBooker(booker);
        oldBooking.setItem(item);
        oldBooking.setStatus(Status.WAITING);

        Mockito.when(bookingRepository.findByIdAndItemOwnerId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(Optional.of(oldBooking));
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class))).thenReturn(oldBooking);

        bookingService.approveOrReject(userId, id, approved);

        verify(bookingRepository).save(bookingArgumentCaptor.capture());
        Booking savedBooking = bookingArgumentCaptor.getValue();
        assertEquals(Status.APPROVED, savedBooking.getStatus());
    }

    @Test
    void approveWhenBookingFoundAndApprovedIsFalseThenUpdatedBooking(){
        Boolean approved = false;

        Long userId = 0L;
        User booker = new User();
        booker.setId(userId);

        Long itemId = 0L;
        Item item = new Item();
        item.setId(itemId);

        Long id = 0L;
        Booking oldBooking = new Booking();
        oldBooking.setId(id);
        oldBooking.setBooker(booker);
        oldBooking.setItem(item);
        oldBooking.setStatus(Status.WAITING);

        Mockito.when(bookingRepository.findByIdAndItemOwnerId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(Optional.of(oldBooking));
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class))).thenReturn(oldBooking);

        bookingService.approveOrReject(userId, id, approved);

        verify(bookingRepository).save(bookingArgumentCaptor.capture());
        Booking savedBooking = bookingArgumentCaptor.getValue();
        assertEquals(Status.REJECTED, savedBooking.getStatus());
    }

    @Test
    void approveWhenBookingNotFoundThenNotUpdatedBooking(){
        Boolean approved = true;

        Long userId = 0L;
        User booker = new User();
        booker.setId(userId);

        Long itemId = 0L;
        Item item = new Item();
        item.setId(itemId);

        Long id = 0L;
        Booking oldBooking = new Booking();
        oldBooking.setId(id);
        oldBooking.setBooker(booker);
        oldBooking.setItem(item);
        oldBooking.setStatus(Status.WAITING);

        Mockito.when(bookingRepository.findByIdAndItemOwnerId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.approveOrReject(userId, id, approved));
        verify(bookingRepository, Mockito.never()).save(Mockito.any(Booking.class));
    }

    @Test
    void approveWhenBookingFoundAndApprovedIsTrueButBookingStatusIsApprovedThenNotUpdatedBooking(){
        Boolean approved = true;

        Long userId = 0L;
        User booker = new User();
        booker.setId(userId);

        Long itemId = 0L;
        Item item = new Item();
        item.setId(itemId);

        Long id = 0L;
        Booking oldBooking = new Booking();
        oldBooking.setId(id);
        oldBooking.setBooker(booker);
        oldBooking.setItem(item);
        oldBooking.setStatus(Status.APPROVED);

        Mockito.when(bookingRepository.findByIdAndItemOwnerId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(Optional.of(oldBooking));

        assertThrows(BookingAlreadyApprovedException.class, () -> bookingService.approveOrReject(userId, id, approved));
        verify(bookingRepository, Mockito.never()).save(Mockito.any(Booking.class));
    }

    @Test
    void getByIdWhenBookingFoundAndRequestFromBookerOrOwnerThenReturnedBooking(){
        Long userId = 0L;
        User booker = new User();
        booker.setId(userId);

        Long itemId = 0L;
        Item item = new Item();
        item.setId(itemId);

        Long id = 0L;
        Booking expectedBooking = new Booking();
        expectedBooking.setId(id);
        expectedBooking.setBooker(booker);
        expectedBooking.setItem(item);
        BookingDtoForResponse expectedBookingDto = BookingMapper.toDto(expectedBooking);
        Mockito.when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(expectedBooking));

        BookingDtoForResponse booking = bookingService.getById(userId, id);

        assertEquals(expectedBookingDto, booking);
    }

    @Test
    void getByIdWhenBookingNotFoundThenNotReturnedBooking(){
        Long userId = 0L;
        Long id = 0L;
        Mockito.when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,() -> bookingService.getById(userId, id));
    }

    @Test
    void getByIdWhenBookingFoundButRequestNotFromBookerOrOwnerThenNotReturnedBooking(){
        Long userId = 99L;

        Long bookerId = 0L;
        User booker = new User();
        booker.setId(bookerId);

        Long itemId = 0L;
        Item item = new Item();
        item.setId(itemId);
        User owner = new User();
        owner.setId(1L);
        item.setOwner(owner);

        Long id = 0L;
        Booking expectedBooking = new Booking();
        expectedBooking.setId(id);
        expectedBooking.setBooker(booker);
        expectedBooking.setItem(item);
        Mockito.when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(expectedBooking));

        assertThrows(EntityNotFoundException.class,() -> bookingService.getById(userId, id));
    }

    


}