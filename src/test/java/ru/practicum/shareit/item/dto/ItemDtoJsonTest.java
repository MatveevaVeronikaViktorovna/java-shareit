package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemDto> json;
    private final ItemDto itemDto = new ItemDto();

    @Test
    void testItemDtoId() throws Exception {
        itemDto.setId(1L);

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
    }

    @Test
    void testItemDtoName() throws Exception {
        itemDto.setName("name");

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("name");
    }

    @Test
    void testItemDtoDescription() throws Exception {
        itemDto.setDescription("description");

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
    }

    @Test
    void testItemDtoAvailable() throws Exception {
        itemDto.setAvailable(true);

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
    }

    @Test
    void testItemDtoComments() throws Exception {
        itemDto.setComments(Collections.emptyList());

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathValue("$.comments").isEqualTo(Collections.emptyList());
    }

    @Test
    void testItemDtoRequestId() throws Exception {
        itemDto.setRequestId(1L);

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }

}