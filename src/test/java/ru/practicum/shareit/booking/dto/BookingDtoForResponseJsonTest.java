package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoForResponseJsonTest {

    @Autowired
    private JacksonTester<BookingDtoForResponse> json;

    @Test
    void testBookingDtoForResponse() throws Exception {
        BookingDtoForResponse bookingDto = new BookingDtoForResponse();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.of(2023, 7, 6, 13, 14, 0));
        bookingDto.setEnd(LocalDateTime.of(2023, 7, 16, 13, 14, 0));
        bookingDto.setBooker(new UserDto());
        bookingDto.setItem(new ItemDto());
        bookingDto.setStatus(Status.WAITING);

        JsonContent<BookingDtoForResponse> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-07-06T13:14:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2023-07-16T13:14:00");
        assertThat(result).extractingJsonPathValue("$.status").isEqualTo(Status.WAITING.toString());
    }

}