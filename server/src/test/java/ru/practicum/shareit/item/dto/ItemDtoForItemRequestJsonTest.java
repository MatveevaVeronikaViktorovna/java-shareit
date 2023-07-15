package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoForItemRequestJsonTest {

    @Autowired
    private JacksonTester<ItemDtoForItemRequest> json;
    private final ItemDtoForItemRequest itemDto = new ItemDtoForItemRequest();

    @Test
    void testItemDtoForItemRequestId() throws Exception {
        itemDto.setId(1L);

        JsonContent<ItemDtoForItemRequest> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
    }

    @Test
    void testItemDtoForItemRequestName() throws Exception {
        itemDto.setName("name");

        JsonContent<ItemDtoForItemRequest> result = json.write(itemDto);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("name");
    }

    @Test
    void testItemDtoForItemRequestDescription() throws Exception {
        itemDto.setDescription("description");

        JsonContent<ItemDtoForItemRequest> result = json.write(itemDto);

        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
    }

    @Test
    void testItemDtoForItemRequestAvailable() throws Exception {
        itemDto.setAvailable(true);

        JsonContent<ItemDtoForItemRequest> result = json.write(itemDto);

        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
    }

    @Test
    void testItemDtoForItemRequestRequestId() throws Exception {
        itemDto.setRequestId(1L);

        JsonContent<ItemDtoForItemRequest> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }

}