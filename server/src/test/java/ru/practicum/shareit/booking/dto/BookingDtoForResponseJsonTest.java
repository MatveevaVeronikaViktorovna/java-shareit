package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoForResponseJsonTest {

    @Autowired
    private JacksonTester<BookingDtoForResponse> json;
    private final BookingDtoForResponse bookingDto = new BookingDtoForResponse();

    @Test
    void testBookingDtoForResponseId() throws Exception {
        bookingDto.setId(1L);

        JsonContent<BookingDtoForResponse> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
    }

    @Test
    void testBookingDtoForResponseStart() throws Exception {
        bookingDto.setStart(LocalDateTime.of(2023, 7, 6, 13, 14, 0));

        JsonContent<BookingDtoForResponse> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-07-06T13:14:00");
    }

    @Test
    void testBookingDtoForResponseEnd() throws Exception {
        bookingDto.setEnd(LocalDateTime.of(2023, 7, 16, 13, 14, 0));

        JsonContent<BookingDtoForResponse> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2023-07-16T13:14:00");
    }

    @Test
    void testBookingDtoForResponseStatus() throws Exception {
        bookingDto.setStatus(Status.WAITING);

        JsonContent<BookingDtoForResponse> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathValue("$.status").isEqualTo(Status.WAITING.toString());
    }

}