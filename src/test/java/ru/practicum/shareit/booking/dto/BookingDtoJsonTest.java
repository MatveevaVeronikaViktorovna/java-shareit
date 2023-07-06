package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testBookingDto() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.of(2023, 7, 6, 13, 14, 0));
        bookingDto.setEnd(LocalDateTime.of(2023, 7, 16, 13, 14, 0));
        bookingDto.setItemId(1L);

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-07-06T13:14:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2023-07-16T13:14:00");
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
    }

}