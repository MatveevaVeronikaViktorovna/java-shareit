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
class BookingDtoForItemJsonTest {

    @Autowired
    private JacksonTester<BookingDtoForItem> json;
    private final BookingDtoForItem bookingDto = new BookingDtoForItem();

    @Test
    void testBookingDtoForItemId() throws Exception {
        bookingDto.setId(1L);

        JsonContent<BookingDtoForItem> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
    }

    @Test
    void testBookingDtoForItemStart() throws Exception {
        bookingDto.setStart(LocalDateTime.of(2023, 7, 6, 13, 14, 0));

        JsonContent<BookingDtoForItem> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-07-06T13:14:00");
    }

    @Test
    void testBookingDtoForItemEnd() throws Exception {
        bookingDto.setEnd(LocalDateTime.of(2023, 7, 16, 13, 14, 0));

        JsonContent<BookingDtoForItem> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2023-07-16T13:14:00");
    }

    @Test
    void testBookingDtoForItemBookerId() throws Exception {
        bookingDto.setBookerId(1L);

        JsonContent<BookingDtoForItem> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(1);
    }

    @Test
    void testBookingDtoForItemStatus() throws Exception {
        bookingDto.setStatus(Status.WAITING);

        JsonContent<BookingDtoForItem> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathValue("$.status").isEqualTo(Status.WAITING.toString());
    }

}