package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Item;

@UtilityClass
public class ItemMapper {

    public Item toItem(ItemDto dto) {
        if (dto == null) return null;
        else {
            Item item = new Item();
            item.setName(dto.getName());
            item.setDescription(dto.getDescription());
            item.setAvailable(dto.getAvailable());
            return item;
        }
    }

    public ItemDto toDto(Item item) {
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

    public ItemDtoForItemRequest toDtoForItemRequest(Item item) {
        ItemDtoForItemRequest itemDto = new ItemDtoForItemRequest();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setRequestId(item.getRequest().getId());
        return itemDto;
    }

}
