package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoJsonTest {

    @Autowired
    private JacksonTester<CommentDto> json;
    private final CommentDto commentDto = new CommentDto();

    @Test
    void testCommentDtoId() throws Exception {
        commentDto.setId(1L);

        JsonContent<CommentDto> result = json.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
    }

    @Test
    void testCommentDtoText() throws Exception {
        commentDto.setText("text");

        JsonContent<CommentDto> result = json.write(commentDto);

        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("text");
    }

    @Test
    void testCommentDtoAuthorName() throws Exception {
        commentDto.setAuthorName("name");

        JsonContent<CommentDto> result = json.write(commentDto);

        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("name");
    }

    @Test
    void testCommentDtoCreated() throws Exception {
        commentDto.setCreated(LocalDateTime.of(2023, 7, 6, 13, 14, 0));

        JsonContent<CommentDto> result = json.write(commentDto);

        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2023-07-06T13:14:00");
    }

}