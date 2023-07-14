package ru.practicum.shareit.request.dto;

import org.mapstruct.Mapper;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Mapper
public interface ItemRequestDtoMapper {
    ItemRequest dtoToItemRequest(ItemRequestDto dto);

    ItemRequestDtoForResponse itemRequestToDto(ItemRequest itemRequest);

    List<ItemRequestDtoForResponse> itemRequestToDto(List<ItemRequest> itemRequests);
}
