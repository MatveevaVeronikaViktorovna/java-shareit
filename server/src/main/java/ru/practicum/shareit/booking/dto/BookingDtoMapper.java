package ru.practicum.shareit.booking.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingDtoMapper {
    Booking dtoToBooking(BookingDto dto);

    BookingDtoForResponse bookingToDto(Booking booking);

    List<BookingDtoForResponse> bookingToDto(List<Booking> bookings);

    @Mapping(target = "bookerId", source = "booker")
    BookingDtoForItem bookingToDtoForItem(Booking booking);

    default Long mapBookerToBookerId(User booker) {
        return booker.getId();
    }

}
