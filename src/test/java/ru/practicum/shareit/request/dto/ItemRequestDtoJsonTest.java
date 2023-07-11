package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoJsonTest {
    @Autowired
    private JacksonTester<ItemRequestDto> json;
    private final ItemRequestDto itemRequestDto = new ItemRequestDto();

    @Test
    void testItemRequestDtoId() throws Exception {
        itemRequestDto.setId(1L);

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
    }

    @Test
    void testItemRequestDtoDescription() throws Exception {
        itemRequestDto.setDescription("description");

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
    }

    @Test
    void testItemRequestDtoCreated() throws Exception {
        itemRequestDto.setCreated(LocalDateTime.of(2023, 7, 6, 13, 14, 0));

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2023-07-06T13:14:00");
    }

}