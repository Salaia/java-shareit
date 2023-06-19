package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemRequestDtoInputTest {
    @Autowired
    JacksonTester<ItemRequestDtoInput> jacksonTester;
    static ItemRequestDtoInput itemRequestDtoInput;

    @BeforeEach
    public void setUp() {
        itemRequestDtoInput = ItemRequestDtoInput.builder()
                .description("description")
                .build();
    }

    @Test
    public void createDtoSuccess() throws IOException {
        JsonContent<ItemRequestDtoInput> result = jacksonTester.write(itemRequestDtoInput);

        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
    }

}