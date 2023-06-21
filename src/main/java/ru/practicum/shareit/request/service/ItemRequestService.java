package ru.practicum.shareit.request.service;

import org.springframework.web.bind.annotation.RequestHeader;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoForResponse;

import java.util.List;

import static ru.practicum.shareit.booking.controller.BookingController.HEADER;

public interface ItemRequestService {

    ItemRequestDtoForResponse create(Long userId, ItemRequestDto itemRequestDto);
    List<ItemRequestDtoForResponse> getAllByRequestor(Long userId);
}
