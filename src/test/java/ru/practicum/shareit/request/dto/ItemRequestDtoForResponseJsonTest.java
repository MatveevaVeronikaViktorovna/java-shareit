package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoForResponseJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDtoForResponse> json;

    @Test
    void testItemRequestDtoForResponse() throws Exception {
        ItemRequestDtoForResponse itemRequestDto = new ItemRequestDtoForResponse();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("description");
        itemRequestDto.setCreated(LocalDateTime.of(2023, 7, 6, 13, 14, 0));
        itemRequestDto.setItems(Collections.emptyList());

        JsonContent<ItemRequestDtoForResponse> result = json.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2023-07-06T13:14:00");
        assertThat(result).extractingJsonPathValue("$.items").isEqualTo(Collections.emptyList());
    }
}