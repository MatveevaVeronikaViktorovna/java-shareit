package ru.practicum.shareit.pagination;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class CustomPageRequest extends PageRequest {

    protected CustomPageRequest(int from, int size) {
        super(from / size, size, Sort.unsorted());
    }

    public static PageRequest of(int from, int size) {
        return of(from / size, size, Sort.unsorted());
    }

}
