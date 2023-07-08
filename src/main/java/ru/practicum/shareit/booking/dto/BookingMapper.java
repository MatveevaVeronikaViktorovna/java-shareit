package ru.practicum.shareit.booking.dto;

import lombok.experimental.UtilityClass;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.dto.UserDtoMapper;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class BookingMapper {

    private UserDtoMapper mapper = Mappers.getMapper(UserDtoMapper.class);

    public Booking toBooking(BookingDto dto) {
        if (dto == null) return null;
        else {
            Booking booking = new Booking();
            booking.setStart(dto.getStart());
            booking.setEnd(dto.getEnd());
            return booking;
        }
    }

    public BookingDtoForResponse toDto(Booking booking) {
        BookingDtoForResponse bookingDto = new BookingDtoForResponse();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setBooker(mapper.userToDto(booking.getBooker()));
        bookingDto.setItem(ItemMapper.toDto(booking.getItem()));
        bookingDto.setStatus(booking.getStatus());
        return bookingDto;
    }

    public List<BookingDtoForResponse> toDto(List<Booking> bookings) {
        List<BookingDtoForResponse> result = new ArrayList<>();

        for (Booking booking : bookings) {
            result.add(toDto(booking));
        }
        return result;
    }

    public BookingDtoForItem toDtoForItem(Booking booking) {
        BookingDtoForItem bookingDto = new BookingDtoForItem();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setBookerId(booking.getBooker().getId());
        bookingDto.setStatus(booking.getStatus());
        return bookingDto;
    }

}
