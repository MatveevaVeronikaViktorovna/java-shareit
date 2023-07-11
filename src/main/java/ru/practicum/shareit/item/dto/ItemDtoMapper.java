package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Mapper
public interface ItemDtoMapper {
    Item dtoToItem(ItemDto dto);
    ItemDto itemToDto(Item item);
    List<ItemDto> itemToDto(List<Item> items);
    ItemDtoForItemRequest itemToDtoForItemRequest(Item item);
}
