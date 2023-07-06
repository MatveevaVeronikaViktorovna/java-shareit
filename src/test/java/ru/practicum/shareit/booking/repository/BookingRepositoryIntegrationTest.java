package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class BookingRepositoryIntegrationTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ItemRequestRepository itemRequestRepository;

    @Autowired
    BookingRepository bookingRepository;

    Pageable page = PageRequest.of(0, 20);
    User owner;
    User requestor;
    User booker;
    ItemRequest request1;
    Item item1;
    Booking earliestBooking;
    Booking mediumBooking;
    Booking latestBooking;

    @BeforeEach
    public void addBookings() {
        owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@yandex.ru");
        userRepository.save(owner);

        requestor = new User();
        requestor.setName("requestor");
        requestor.setEmail("requestor@yandex.ru");
        userRepository.save(requestor);

        booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@yandex.ru");
        userRepository.save(booker);

        request1 = new ItemRequest();
        request1.setDescription("description1");
        request1.setRequestor(requestor);
        request1.setCreated(LocalDateTime.now());
        itemRequestRepository.save(request1);

        item1 = new Item();
        item1.setName("name1");
        item1.setDescription("description1");
        item1.setAvailable(true);
        item1.setOwner(owner);
        item1.setRequest(request1);
        itemRepository.save(item1);

        earliestBooking = new Booking();
        earliestBooking.setStart(LocalDateTime.now());
        earliestBooking.setEnd(LocalDateTime.now().plusDays(1L));
        earliestBooking.setItem(item1);
        earliestBooking.setBooker(booker);
        earliestBooking.setStatus(Status.WAITING);
        bookingRepository.save(earliestBooking);

        mediumBooking = new Booking();
        mediumBooking.setStart(LocalDateTime.now().plusDays(2L));
        mediumBooking.setEnd(LocalDateTime.now().plusDays(3L));
        mediumBooking.setItem(item1);
        mediumBooking.setBooker(booker);
        mediumBooking.setStatus(Status.APPROVED);
        bookingRepository.save(mediumBooking);

        latestBooking = new Booking();
        latestBooking.setStart(LocalDateTime.now().plusDays(4L));
        latestBooking.setEnd(LocalDateTime.now().plusDays(5L));
        latestBooking.setItem(item1);
        latestBooking.setBooker(booker);
        latestBooking.setStatus(Status.REJECTED);
        bookingRepository.save(latestBooking);
    }

    @Test
    void findByIdAndItemOwnerId() {
        Optional<Booking> booking = bookingRepository.findByIdAndItemOwnerId(earliestBooking.getId(), owner.getId());

        assertTrue(booking.isPresent());
        assertEquals(earliestBooking.getId(), booking.get().getId());
    }

    @Test
    void findAllByBookerIdOrderByStartDesc() {
        List<Booking> actualBookings = bookingRepository.findAllByBookerIdOrderByStartDesc(booker.getId(), page);

        assertEquals(3, actualBookings.size());
        assertEquals(latestBooking.getId(), actualBookings.get(0).getId());
        assertEquals(mediumBooking.getId(), actualBookings.get(1).getId());
        assertEquals(earliestBooking.getId(), actualBookings.get(2).getId());
    }

    @Test
    void findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc() {
        List<Booking> actualBookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(booker.getId(), LocalDateTime.now().plusDays(1L), LocalDateTime.now(), page);

        assertEquals(1, actualBookings.size());
        assertEquals(earliestBooking.getId(), actualBookings.get(0).getId());
    }

    @Test
    void findAllByBookerIdAndEndBeforeOrderByStartDesc() {
        List<Booking> actualBookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(booker.getId(), LocalDateTime.now().plusDays(4L), page);

        assertEquals(2, actualBookings.size());
        assertEquals(mediumBooking.getId(), actualBookings.get(0).getId());
        assertEquals(earliestBooking.getId(), actualBookings.get(1).getId());
    }

    @Test
    void findAllByBookerIdAndStartAfterOrderByStartDesc() {
        List<Booking> actualBookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(booker.getId(), LocalDateTime.now().plusDays(1L), page);

        assertEquals(2, actualBookings.size());
        assertEquals(latestBooking.getId(), actualBookings.get(0).getId());
        assertEquals(mediumBooking.getId(), actualBookings.get(1).getId());
    }

    @Test
    void findAllByBookerIdAndStatusOrderByStartDesc() {
        List<Booking> actualBookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(booker.getId(), Status.WAITING, page);

        assertEquals(1, actualBookings.size());
        assertEquals(earliestBooking.getId(), actualBookings.get(0).getId());
    }

    @Test
    void findAllByItemOwnerIdOrderByStartDesc() {
        List<Booking> actualBookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(owner.getId(), page);

        assertEquals(3, actualBookings.size());
        assertEquals(latestBooking.getId(), actualBookings.get(0).getId());
        assertEquals(mediumBooking.getId(), actualBookings.get(1).getId());
        assertEquals(earliestBooking.getId(), actualBookings.get(2).getId());

    }

    @Test
    void findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc() {
        List<Booking> actualBookings = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(owner.getId(), LocalDateTime.now().plusDays(1L), LocalDateTime.now(), page);

        assertEquals(1, actualBookings.size());
        assertEquals(earliestBooking.getId(), actualBookings.get(0).getId());
    }

    @Test
    void findAllByItemOwnerIdAndEndBeforeOrderByStartDesc() {
        List<Booking> actualBookings = bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(owner.getId(), LocalDateTime.now().plusDays(4L), page);

        assertEquals(2, actualBookings.size());
        assertEquals(mediumBooking.getId(), actualBookings.get(0).getId());
        assertEquals(earliestBooking.getId(), actualBookings.get(1).getId());
    }

    @Test
    void findAllByItemOwnerIdAndStartAfterOrderByStartDesc() {
        List<Booking> actualBookings = bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(owner.getId(), LocalDateTime.now().plusDays(1L), page);

        assertEquals(2, actualBookings.size());
        assertEquals(latestBooking.getId(), actualBookings.get(0).getId());
        assertEquals(mediumBooking.getId(), actualBookings.get(1).getId());
    }

    @Test
    void findAllByItemOwnerIdAndStatusOrderByStartDesc() {
        List<Booking> actualBookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(owner.getId(), Status.WAITING, page);

        assertEquals(1, actualBookings.size());
        assertEquals(earliestBooking.getId(), actualBookings.get(0).getId());
    }

    @Test
    void findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc() {
        Optional<Booking> booking = bookingRepository.findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(item1.getId(), LocalDateTime.now().plusDays(20), Status.WAITING);

        assertTrue(booking.isPresent());
        assertEquals(earliestBooking.getId(), booking.get().getId());
    }

    @Test
    void findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc() {
        Optional<Booking> booking = bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(item1.getId(), LocalDateTime.now().minusDays(20), Status.WAITING);

        assertTrue(booking.isPresent());
        assertEquals(earliestBooking.getId(), booking.get().getId());
    }

    @Test
    void findAllByBookerIdAndItemIdAndEndBeforeOrderByStartDesc() {
        List<Booking> actualBookings = bookingRepository.findAllByBookerIdAndItemIdAndEndBeforeOrderByStartDesc(booker.getId(), item1.getId(), LocalDateTime.now().plusDays(90));

        assertEquals(3, actualBookings.size());
        assertEquals(latestBooking.getId(), actualBookings.get(0).getId());
        assertEquals(mediumBooking.getId(), actualBookings.get(1).getId());
        assertEquals(earliestBooking.getId(), actualBookings.get(2).getId());
    }

    @AfterEach
    public void deleteItems() {
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();
    }
}