package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import java.util.List;
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
    private BookingDto requestBookingDto;
    private BookingDtoForResponse expectedBookingDto;
    Long id;
    private Booking expectedBooking;
    private Long userId;
    private User booker;
    private Long itemId;
    private Item item;
    private User owner;
    private Integer from = 0;
    private Integer size = 10;

    @BeforeEach
    public void addBookings() {
        userId = 0L;
        booker = new User();
        booker.setId(userId);

        itemId = 0L;
        item = new Item();
        item.setId(itemId);
        item.setAvailable(true);
        owner = new User();
        owner.setId(1L);
        item.setOwner(owner);

        requestBookingDto = new BookingDto();
        requestBookingDto.setItemId(itemId);

        id = 0L;
        expectedBooking = new Booking();
        expectedBooking.setBooker(booker);
        expectedBooking.setItem(item);

        expectedBookingDto = BookingMapper.toDto(expectedBooking);

        from = 0;
        size = 10;
    }

    @Test
    void createWhenItemFoundAndAvailableAndBookerFoundAndNotOwnerThenSavedBooking() {
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booker));
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class))).thenReturn(expectedBooking);

        BookingDtoForResponse booking = bookingService.create(userId, requestBookingDto);

        assertEquals(expectedBookingDto, booking);
        verify(bookingRepository).save(Mockito.any(Booking.class));
    }

    @Test
    void createWhenItemNotFoundThenNotSavedBooking() {
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.create(userId, requestBookingDto));

        verify(bookingRepository, Mockito.never()).save(Mockito.any(Booking.class));
    }

    @Test
    void createWhenUserNotFoundThenNotSavedBooking() {
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.create(userId, requestBookingDto));

        verify(bookingRepository, Mockito.never()).save(Mockito.any(Booking.class));
    }

    @Test
    void createWhenItemFoundButNotAvailableAndBookerFoundAndNotOwnerThenNotSavedBooking() {
        item.setAvailable(false);
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));

        assertThrows(ItemNotAvailableException.class, () -> bookingService.create(userId, requestBookingDto));

        verify(bookingRepository, Mockito.never()).save(Mockito.any(Booking.class));
    }

    @Test
    void createWhenItemFoundAndAvailableAndBookerFoundButEqualOwnerThenNotSavedBooking() {
        owner.setId(userId);
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booker));

        assertThrows(EntityNotFoundException.class, () -> bookingService.create(userId, requestBookingDto));

        verify(bookingRepository, Mockito.never()).save(Mockito.any(Booking.class));
    }

    @Test
    void approveWhenBookingFoundAndApprovedIsTrueThenUpdatedBooking() {
        Boolean approved = true;
        expectedBooking.setId(id);
        expectedBooking.setStatus(Status.WAITING);

        Mockito.when(bookingRepository.findByIdAndItemOwnerId(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Optional.of(expectedBooking));
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class))).thenReturn(expectedBooking);

        bookingService.approveOrReject(userId, id, approved);

        verify(bookingRepository).save(bookingArgumentCaptor.capture());
        Booking savedBooking = bookingArgumentCaptor.getValue();
        assertEquals(Status.APPROVED, savedBooking.getStatus());
    }

    @Test
    void approveWhenBookingFoundAndApprovedIsFalseThenUpdatedBooking() {
        Boolean approved = false;
        expectedBooking.setId(id);
        expectedBooking.setStatus(Status.WAITING);

        Mockito.when(bookingRepository.findByIdAndItemOwnerId(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Optional.of(expectedBooking));
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class))).thenReturn(expectedBooking);

        bookingService.approveOrReject(userId, id, approved);

        verify(bookingRepository).save(bookingArgumentCaptor.capture());
        Booking savedBooking = bookingArgumentCaptor.getValue();
        assertEquals(Status.REJECTED, savedBooking.getStatus());
    }

    @Test
    void approveWhenBookingNotFoundThenNotUpdatedBooking() {
        Boolean approved = true;
        expectedBooking.setId(id);
        expectedBooking.setStatus(Status.WAITING);
        Mockito.when(bookingRepository.findByIdAndItemOwnerId(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.approveOrReject(userId, id, approved));
        verify(bookingRepository, Mockito.never()).save(Mockito.any(Booking.class));
    }

    @Test
    void approveWhenBookingFoundAndApprovedIsTrueButBookingStatusIsApprovedThenNotUpdatedBooking() {
        Boolean approved = true;
        expectedBooking.setId(id);
        expectedBooking.setStatus(Status.APPROVED);

        Mockito.when(bookingRepository.findByIdAndItemOwnerId(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Optional.of(expectedBooking));

        assertThrows(BookingAlreadyApprovedException.class, () -> bookingService.approveOrReject(userId, id, approved));
        verify(bookingRepository, Mockito.never()).save(Mockito.any(Booking.class));
    }

    @Test
    void getByIdWhenBookingFoundAndRequestFromBookerOrOwnerThenReturnedBooking() {
        Mockito.when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(expectedBooking));

        BookingDtoForResponse booking = bookingService.getById(userId, id);

        assertEquals(expectedBookingDto, booking);
    }

    @Test
    void getByIdWhenBookingNotFoundThenNotReturnedBooking() {
        Mockito.when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.getById(userId, id));
    }

    @Test
    void getByIdWhenBookingFoundButRequestNotFromBookerOrOwnerThenNotReturnedBooking() {
        Long requestorId = 99L;

        Mockito.when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(expectedBooking));

        assertThrows(EntityNotFoundException.class, () -> bookingService.getById(requestorId, id));
    }

    @Test
    void getAllByBookerWhenBookerNotFoundThenNotReturnedListOfBookings() {
        Mockito.when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> bookingService.getAllByBooker(userId, State.ALL, from, size));
    }

    @Test
    void getAllByBookerWhenBookerFoundAndStateIsAllThenReturnedListOfBookings() {
        Long userId = 0L;
        Integer from = 0;
        Integer size = 10;
        Pageable page = PageRequest.of(from / size, size);

        Booking currentBooking = new Booking();
        currentBooking.setId(1L);
        Booking pastBooking = new Booking();
        pastBooking.setId(2L);
        Booking futureBooking = new Booking();
        currentBooking.setId(3L);
        Booking waitingBooking = new Booking();
        currentBooking.setId(4L);
        Booking rejectedBooking = new Booking();
        currentBooking.setId(5L);
        List<Booking> allBookings = List.of(currentBooking, pastBooking, futureBooking, waitingBooking,
                rejectedBooking);

        List<Booking> expectedBookings = allBookings;
        expectedBookings.forEach(booking -> booking.setBooker(new User()));
        expectedBookings.forEach(booking -> booking.setItem(new Item()));
        List<BookingDtoForResponse> expectedBookingsDto = BookingMapper.toDto(expectedBookings);
        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(bookingRepository.findAllByBookerIdOrderByStartDesc(userId, page)).thenReturn(expectedBookings);

        List<BookingDtoForResponse> bookings = bookingService.getAllByBooker(userId, State.ALL, from, size);

        assertEquals(expectedBookingsDto, bookings);
    }

    @Test
    void getAllByBookerWhenBookerFoundAndStateIsCurrentThenReturnedListOfBookings() {
        Long userId = 0L;
        Integer from = 0;
        Integer size = 10;
        Pageable page = PageRequest.of(from / size, size);

        Booking currentBooking = new Booking();
        currentBooking.setId(1L);
        Booking pastBooking = new Booking();
        pastBooking.setId(2L);
        Booking futureBooking = new Booking();
        currentBooking.setId(3L);
        Booking waitingBooking = new Booking();
        currentBooking.setId(4L);
        Booking rejectedBooking = new Booking();
        currentBooking.setId(5L);
        List<Booking> allBookings = List.of(currentBooking, pastBooking, futureBooking, waitingBooking,
                rejectedBooking);

        List<Booking> expectedBookings = List.of(currentBooking);
        expectedBookings.forEach(booking -> booking.setBooker(new User()));
        expectedBookings.forEach(booking -> booking.setItem(new Item()));
        List<BookingDtoForResponse> expectedBookingsDto = BookingMapper.toDto(expectedBookings);
        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Mockito.anyLong(),
                        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class)))
                .thenReturn(expectedBookings);

        List<BookingDtoForResponse> bookings = bookingService.getAllByBooker(userId, State.CURRENT, from, size);

        assertEquals(expectedBookingsDto, bookings);
    }

    @Test
    void getAllByBookerWhenBookerFoundAndStateIsPastThenReturnedListOfBookings() {
        Long userId = 0L;
        Integer from = 0;
        Integer size = 10;
        Pageable page = PageRequest.of(from / size, size);

        Booking currentBooking = new Booking();
        currentBooking.setId(1L);
        Booking pastBooking = new Booking();
        pastBooking.setId(2L);
        Booking futureBooking = new Booking();
        currentBooking.setId(3L);
        Booking waitingBooking = new Booking();
        currentBooking.setId(4L);
        Booking rejectedBooking = new Booking();
        currentBooking.setId(5L);
        List<Booking> allBookings = List.of(currentBooking, pastBooking, futureBooking, waitingBooking,
                rejectedBooking);

        List<Booking> expectedBookings = List.of(pastBooking);
        expectedBookings.forEach(booking -> booking.setBooker(new User()));
        expectedBookings.forEach(booking -> booking.setItem(new Item()));
        List<BookingDtoForResponse> expectedBookingsDto = BookingMapper.toDto(expectedBookings);
        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(Mockito.anyLong(),
                Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class))).thenReturn(expectedBookings);

        List<BookingDtoForResponse> bookings = bookingService.getAllByBooker(userId, State.PAST, from, size);

        assertEquals(expectedBookingsDto, bookings);
    }

    @Test
    void getAllByBookerWhenBookerFoundAndStateIsFutureThenReturnedListOfBookings() {
        Long userId = 0L;
        Integer from = 0;
        Integer size = 10;
        Pageable page = PageRequest.of(from / size, size);

        Booking currentBooking = new Booking();
        currentBooking.setId(1L);
        Booking pastBooking = new Booking();
        pastBooking.setId(2L);
        Booking futureBooking = new Booking();
        currentBooking.setId(3L);
        Booking waitingBooking = new Booking();
        currentBooking.setId(4L);
        Booking rejectedBooking = new Booking();
        currentBooking.setId(5L);
        List<Booking> allBookings = List.of(currentBooking, pastBooking, futureBooking, waitingBooking,
                rejectedBooking);

        List<Booking> expectedBookings = List.of(futureBooking);
        expectedBookings.forEach(booking -> booking.setBooker(new User()));
        expectedBookings.forEach(booking -> booking.setItem(new Item()));
        List<BookingDtoForResponse> expectedBookingsDto = BookingMapper.toDto(expectedBookings);
        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(Mockito.anyLong(),
                Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class))).thenReturn(expectedBookings);

        List<BookingDtoForResponse> bookings = bookingService.getAllByBooker(userId, State.FUTURE, from, size);

        assertEquals(expectedBookingsDto, bookings);
    }

    @Test
    void getAllByBookerWhenBookerFoundAndStateIsWaitingThenReturnedListOfBookings() {
        Long userId = 0L;
        Integer from = 0;
        Integer size = 10;
        Pageable page = PageRequest.of(from / size, size);

        Booking currentBooking = new Booking();
        currentBooking.setId(1L);
        Booking pastBooking = new Booking();
        pastBooking.setId(2L);
        Booking futureBooking = new Booking();
        currentBooking.setId(3L);
        Booking waitingBooking = new Booking();
        currentBooking.setId(4L);
        Booking rejectedBooking = new Booking();
        currentBooking.setId(5L);
        List<Booking> allBookings = List.of(currentBooking, pastBooking, futureBooking, waitingBooking, rejectedBooking);

        List<Booking> expectedBookings = List.of(waitingBooking);
        expectedBookings.forEach(booking -> booking.setBooker(new User()));
        expectedBookings.forEach(booking -> booking.setItem(new Item()));
        List<BookingDtoForResponse> expectedBookingsDto = BookingMapper.toDto(expectedBookings);
        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(Mockito.anyLong(),
                Mockito.any(Status.class), Mockito.any(Pageable.class))).thenReturn(expectedBookings);

        List<BookingDtoForResponse> bookings = bookingService.getAllByBooker(userId, State.WAITING, from, size);

        assertEquals(expectedBookingsDto, bookings);
    }

    @Test
    void getAllByBookerWhenBookerFoundAndStateIsRejectedThenReturnedListOfBookings() {
        Long userId = 0L;
        Integer from = 0;
        Integer size = 10;
        Pageable page = PageRequest.of(from / size, size);

        Booking currentBooking = new Booking();
        currentBooking.setId(1L);
        Booking pastBooking = new Booking();
        pastBooking.setId(2L);
        Booking futureBooking = new Booking();
        currentBooking.setId(3L);
        Booking waitingBooking = new Booking();
        currentBooking.setId(4L);
        Booking rejectedBooking = new Booking();
        currentBooking.setId(5L);
        List<Booking> allBookings = List.of(currentBooking, pastBooking, futureBooking, waitingBooking,
                rejectedBooking);

        List<Booking> expectedBookings = List.of(rejectedBooking);
        expectedBookings.forEach(booking -> booking.setBooker(new User()));
        expectedBookings.forEach(booking -> booking.setItem(new Item()));
        List<BookingDtoForResponse> expectedBookingsDto = BookingMapper.toDto(expectedBookings);
        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(Mockito.anyLong(),
                Mockito.any(Status.class), Mockito.any(Pageable.class))).thenReturn(expectedBookings);

        List<BookingDtoForResponse> bookings = bookingService.getAllByBooker(userId, State.REJECTED, from, size);

        assertEquals(expectedBookingsDto, bookings);
    }


    @Test
    void getAllByOwnerWhenOwnerNotFoundThenNotReturnedListOfBookings() {
        Long userId = 0L;
        Integer from = 0;
        Integer size = 10;
        Mockito.when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> bookingService.getAllByOwner(userId, State.ALL, from, size));
    }

    @Test
    void getAllByOwnerWhenOwnerFoundAndStateIsAllThenReturnedListOfBookings() {
        Long userId = 0L;
        Integer from = 0;
        Integer size = 10;
        Pageable page = PageRequest.of(from / size, size);

        Booking currentBooking = new Booking();
        currentBooking.setId(1L);
        Booking pastBooking = new Booking();
        pastBooking.setId(2L);
        Booking futureBooking = new Booking();
        currentBooking.setId(3L);
        Booking waitingBooking = new Booking();
        currentBooking.setId(4L);
        Booking rejectedBooking = new Booking();
        currentBooking.setId(5L);
        List<Booking> allBookings = List.of(currentBooking, pastBooking, futureBooking, waitingBooking,
                rejectedBooking);

        List<Booking> expectedBookings = allBookings;
        expectedBookings.forEach(booking -> booking.setBooker(new User()));
        expectedBookings.forEach(booking -> booking.setItem(new Item()));
        List<BookingDtoForResponse> expectedBookingsDto = BookingMapper.toDto(expectedBookings);
        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId, page)).thenReturn(expectedBookings);

        List<BookingDtoForResponse> bookings = bookingService.getAllByOwner(userId, State.ALL, from, size);

        assertEquals(expectedBookingsDto, bookings);
    }

    @Test
    void getAllByOwnerWhenOwnerFoundAndStateIsCurrentThenReturnedListOfBookings() {
        Long userId = 0L;
        Integer from = 0;
        Integer size = 10;
        Pageable page = PageRequest.of(from / size, size);

        Booking currentBooking = new Booking();
        currentBooking.setId(1L);
        Booking pastBooking = new Booking();
        pastBooking.setId(2L);
        Booking futureBooking = new Booking();
        currentBooking.setId(3L);
        Booking waitingBooking = new Booking();
        currentBooking.setId(4L);
        Booking rejectedBooking = new Booking();
        currentBooking.setId(5L);
        List<Booking> allBookings = List.of(currentBooking, pastBooking, futureBooking, waitingBooking,
                rejectedBooking);

        List<Booking> expectedBookings = List.of(currentBooking);
        expectedBookings.forEach(booking -> booking.setBooker(new User()));
        expectedBookings.forEach(booking -> booking.setItem(new Item()));
        List<BookingDtoForResponse> expectedBookingsDto = BookingMapper.toDto(expectedBookings);
        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Mockito.anyLong(),
                Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class))).thenReturn(expectedBookings);

        List<BookingDtoForResponse> bookings = bookingService.getAllByOwner(userId, State.CURRENT, from, size);

        assertEquals(expectedBookingsDto, bookings);
    }

    @Test
    void getAllByOwnerWhenOwnerFoundAndStateIsPastThenReturnedListOfBookings() {
        Long userId = 0L;
        Integer from = 0;
        Integer size = 10;
        Pageable page = PageRequest.of(from / size, size);

        Booking currentBooking = new Booking();
        currentBooking.setId(1L);
        Booking pastBooking = new Booking();
        pastBooking.setId(2L);
        Booking futureBooking = new Booking();
        currentBooking.setId(3L);
        Booking waitingBooking = new Booking();
        currentBooking.setId(4L);
        Booking rejectedBooking = new Booking();
        currentBooking.setId(5L);
        List<Booking> allBookings = List.of(currentBooking, pastBooking, futureBooking, waitingBooking,
                rejectedBooking);

        List<Booking> expectedBookings = List.of(pastBooking);
        expectedBookings.forEach(booking -> booking.setBooker(new User()));
        expectedBookings.forEach(booking -> booking.setItem(new Item()));
        List<BookingDtoForResponse> expectedBookingsDto = BookingMapper.toDto(expectedBookings);
        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(Mockito.anyLong(),
                Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class))).thenReturn(expectedBookings);

        List<BookingDtoForResponse> bookings = bookingService.getAllByOwner(userId, State.PAST, from, size);

        assertEquals(expectedBookingsDto, bookings);
    }

    @Test
    void getAllByOwnerWhenOwnerFoundAndStateIsFutureThenReturnedListOfBookings() {
        Long userId = 0L;
        Integer from = 0;
        Integer size = 10;
        Pageable page = PageRequest.of(from / size, size);

        Booking currentBooking = new Booking();
        currentBooking.setId(1L);
        Booking pastBooking = new Booking();
        pastBooking.setId(2L);
        Booking futureBooking = new Booking();
        currentBooking.setId(3L);
        Booking waitingBooking = new Booking();
        currentBooking.setId(4L);
        Booking rejectedBooking = new Booking();
        currentBooking.setId(5L);
        List<Booking> allBookings = List.of(currentBooking, pastBooking, futureBooking, waitingBooking,
                rejectedBooking);

        List<Booking> expectedBookings = List.of(futureBooking);
        expectedBookings.forEach(booking -> booking.setBooker(new User()));
        expectedBookings.forEach(booking -> booking.setItem(new Item()));
        List<BookingDtoForResponse> expectedBookingsDto = BookingMapper.toDto(expectedBookings);
        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(Mockito.anyLong(),
                Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class))).thenReturn(expectedBookings);

        List<BookingDtoForResponse> bookings = bookingService.getAllByOwner(userId, State.FUTURE, from, size);

        assertEquals(expectedBookingsDto, bookings);
    }

    @Test
    void getAllByOwnerWhenOwnerFoundAndStateIsWaitingThenReturnedListOfBookings() {
        Long userId = 0L;
        Integer from = 0;
        Integer size = 10;
        Pageable page = PageRequest.of(from / size, size);

        Booking currentBooking = new Booking();
        currentBooking.setId(1L);
        Booking pastBooking = new Booking();
        pastBooking.setId(2L);
        Booking futureBooking = new Booking();
        currentBooking.setId(3L);
        Booking waitingBooking = new Booking();
        currentBooking.setId(4L);
        Booking rejectedBooking = new Booking();
        currentBooking.setId(5L);
        List<Booking> allBookings = List.of(currentBooking, pastBooking, futureBooking, waitingBooking,
                rejectedBooking);

        List<Booking> expectedBookings = List.of(waitingBooking);
        expectedBookings.forEach(booking -> booking.setBooker(new User()));
        expectedBookings.forEach(booking -> booking.setItem(new Item()));
        List<BookingDtoForResponse> expectedBookingsDto = BookingMapper.toDto(expectedBookings);
        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(Mockito.anyLong(),
                Mockito.any(Status.class), Mockito.any(Pageable.class))).thenReturn(expectedBookings);

        List<BookingDtoForResponse> bookings = bookingService.getAllByOwner(userId, State.WAITING, from, size);

        assertEquals(expectedBookingsDto, bookings);
    }

    @Test
    void getAllByOwnerWhenOwnerFoundAndStateIsRejectedThenReturnedListOfBookings() {
        Long userId = 0L;
        Integer from = 0;
        Integer size = 10;
        Pageable page = PageRequest.of(from / size, size);

        Booking currentBooking = new Booking();
        currentBooking.setId(1L);
        Booking pastBooking = new Booking();
        pastBooking.setId(2L);
        Booking futureBooking = new Booking();
        currentBooking.setId(3L);
        Booking waitingBooking = new Booking();
        currentBooking.setId(4L);
        Booking rejectedBooking = new Booking();
        currentBooking.setId(5L);
        List<Booking> allBookings = List.of(currentBooking, pastBooking, futureBooking, waitingBooking, rejectedBooking);

        List<Booking> expectedBookings = List.of(rejectedBooking);
        expectedBookings.forEach(booking -> booking.setBooker(new User()));
        expectedBookings.forEach(booking -> booking.setItem(new Item()));
        List<BookingDtoForResponse> expectedBookingsDto = BookingMapper.toDto(expectedBookings);
        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(Mockito.anyLong(),
                Mockito.any(Status.class), Mockito.any(Pageable.class))).thenReturn(expectedBookings);

        List<BookingDtoForResponse> bookings = bookingService.getAllByOwner(userId, State.REJECTED, from, size);

        assertEquals(expectedBookingsDto, bookings);
    }

}