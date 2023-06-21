package ru.practicum.shareit.request.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserMapper;

@UtilityClass
public class ItemRequestMapper {

    public ItemRequest toItemRequest(ItemRequestDto dto) {
        if (dto == null) return null;
        else {
            ItemRequest itemRequest = new ItemRequest();
            itemRequest.setDescription(dto.getDescription());
            return itemRequest;
        }
    }

    public ItemRequestDtoForResponse toDto(ItemRequest itemRequest) {
        ItemRequestDtoForResponse itemRequestDto = new ItemRequestDtoForResponse();
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setCreated(itemRequest.getCreated());
        return itemRequestDto;
    }

}
