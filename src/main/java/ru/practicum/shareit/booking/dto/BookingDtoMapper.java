package ru.practicum.shareit.booking.dto;

import org.mapstruct.Mapper;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

@Mapper
public interface BookingDtoMapper {
    Booking dtoToBooking(BookingDto dto);
    BookingDtoForResponse bookingToDto(Booking booking);
    List<BookingDtoForResponse> bookingToDto(List<Booking> bookings);
    BookingDtoForItem bookingToDtoForItem(Booking booking);
}
