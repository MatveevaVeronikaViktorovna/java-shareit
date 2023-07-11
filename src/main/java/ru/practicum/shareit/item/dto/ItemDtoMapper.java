package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Mapper
public abstract class ItemDtoMapper {
    public abstract Item dtoToItem(ItemDto dto);
    public ItemDto itemToDto(Item item){
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        if (item.getRequest() != null) {
            itemDto.setRequestId(item.getRequest().getId());
        }
        return itemDto;
    }
    public abstract List<ItemDto> itemToDto(List<Item> items);
    public ItemDtoForItemRequest itemToDtoForItemRequest(Item item){
        ItemDtoForItemRequest itemDto = new ItemDtoForItemRequest();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setRequestId(item.getRequest().getId());
        return itemDto;
    }
}
