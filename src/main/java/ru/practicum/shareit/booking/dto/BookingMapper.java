package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.dto.UserMapper;

@Data
public class BookingMapper {
    public static Booking toBooking(BookingDto dto) {
        if (dto == null) return null;
        else {
            Booking booking = new Booking();
            if (dto.getStart() != null) {
                booking.setStart(dto.getStart());
            }
            if (dto.getEnd() != null) {
                booking.setEnd(dto.getEnd());
            }
            return booking;
        }
    }

    public static BookingDtoForResponse toDto(Booking booking) {
        BookingDtoForResponse bookingDto = new BookingDtoForResponse();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setBooker(UserMapper.toDto(booking.getBooker()));
        bookingDto.setItem(ItemMapper.toDto(booking.getItem()));
        bookingDto.setStatus(booking.getStatus());
        return bookingDto;
    }

}
