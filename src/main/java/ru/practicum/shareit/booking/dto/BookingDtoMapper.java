package ru.practicum.shareit.booking.dto;

import org.mapstruct.Mapper;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

@Mapper
public abstract class BookingDtoMapper {
    public abstract Booking dtoToBooking(BookingDto dto);

    public abstract BookingDtoForResponse bookingToDto(Booking booking);

    public abstract List<BookingDtoForResponse> bookingToDto(List<Booking> bookings);

    public BookingDtoForItem bookingToDtoForItem(Booking booking) {
        BookingDtoForItem bookingDto = new BookingDtoForItem();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setBookerId(booking.getBooker().getId());
        bookingDto.setStatus(booking.getStatus());
        return bookingDto;
    }
}
