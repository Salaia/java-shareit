package ru.practicum.shareit.configuration;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class PaginationParameters extends PageRequest {
    public PaginationParameters(int from, int size, Sort sort) {
        super(from / size, size, sort);
    }
}
