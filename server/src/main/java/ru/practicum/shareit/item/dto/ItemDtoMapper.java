package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemDtoMapper {
    Item dtoToItem(ItemDto dto);

    @Mapping(target = "requestId", source = "request")
    ItemDto itemToDto(Item item);

    List<ItemDto> itemToDto(List<Item> items);

    @Mapping(target = "requestId", source = "request")
    ItemDtoForItemRequest itemToDtoForItemRequest(Item item);

    default Long mapRequestToRequestId(ItemRequest request) {
        if (request != null) {
            return request.getId();
        } else return null;
    }

}
