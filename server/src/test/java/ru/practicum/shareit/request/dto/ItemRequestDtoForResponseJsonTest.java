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
    private final ItemRequestDtoForResponse itemRequestDto = new ItemRequestDtoForResponse();

    @Test
    void testItemRequestDtoForResponseId() throws Exception {
        itemRequestDto.setId(1L);

        JsonContent<ItemRequestDtoForResponse> result = json.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
    }

    @Test
    void testItemRequestDtoForResponseDescription() throws Exception {
        itemRequestDto.setDescription("description");

        JsonContent<ItemRequestDtoForResponse> result = json.write(itemRequestDto);

        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
    }

    @Test
    void testItemRequestDtoForResponseCreated() throws Exception {
        itemRequestDto.setCreated(LocalDateTime.of(2023, 7, 6, 13, 14, 0));

        JsonContent<ItemRequestDtoForResponse> result = json.write(itemRequestDto);

        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2023-07-06T13:14:00");
    }

    @Test
    void testItemRequestDtoForResponseItems() throws Exception {
        itemRequestDto.setItems(Collections.emptyList());

        JsonContent<ItemRequestDtoForResponse> result = json.write(itemRequestDto);

        assertThat(result).extractingJsonPathValue("$.items").isEqualTo(Collections.emptyList());
    }

}