package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findByIdAndItemOwnerId(Long id, Long userId);

    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime someMoment,
                                                                             LocalDateTime anotherMoment,
                                                                             Pageable pageable);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime someMoment,
                                                                Pageable pageable);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime someMoment,
                                                                 Pageable pageable);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long userId, Status status, Pageable pageable);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long userId, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime someMoment,
                                                                                LocalDateTime anotherMoment,
                                                                                Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime someMoment,
                                                                   Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime someMoment,
                                                                    Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long userId, Status status, Pageable pageable);

    Optional<Booking> findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(Long itemId, LocalDateTime someMoment,
                                                                               Status status);

    Optional<Booking> findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(Long itemId, LocalDateTime someMoment,
                                                                             Status status);

    List<Booking> findAllByBookerIdAndItemIdAndEndBeforeOrderByStartDesc(Long userId, Long itemId,
                                                                         LocalDateTime someMoment);
}
