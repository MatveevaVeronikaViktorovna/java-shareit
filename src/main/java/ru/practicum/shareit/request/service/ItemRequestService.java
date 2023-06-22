package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoForResponse;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDtoForResponse create(Long userId, ItemRequestDto itemRequestDto);
    List<ItemRequestDtoForResponse> getAllByRequestor(Long userId);
    ItemRequestDtoForResponse getById(Long userId,Long id);
    List <ItemRequestDtoForResponse> getAll(Long userId, Integer from, Integer size);
}
