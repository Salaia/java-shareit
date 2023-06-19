package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@FieldDefaults(level = AccessLevel.PRIVATE)
class BookingDtoInputTest {

    @Autowired
    JacksonTester<BookingDtoInput> jacksonTester;

    static BookingDtoInput bookingDtoInput;
    static LocalDateTime start = LocalDateTime.of(2023, Month.JANUARY, 5, 12, 15, 30);
    static LocalDateTime end = LocalDateTime.of(2023, Month.JANUARY, 5, 12, 30, 30);

    @BeforeEach
    public void setUp() {
        bookingDtoInput = BookingDtoInput.builder()
                .bookerId(1L)
                .itemId(1L)
                .start(start)
                .end(end)
                .build();
    }

    @Test
    public void createDtoSuccess() throws Exception {
        JsonContent<BookingDtoInput> result = jacksonTester.write(bookingDtoInput);
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(start.toString());
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(end.toString());
    }
}