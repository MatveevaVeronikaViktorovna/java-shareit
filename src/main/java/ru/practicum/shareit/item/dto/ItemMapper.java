package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Item;

@UtilityClass
public class ItemMapper {

    public Item toItem(ItemDto dto) {
        if (dto == null) return null;
        else {
            Item item = new Item();
            if (dto.getName() != null) {
                item.setName(dto.getName());
            }
            if (dto.getDescription() != null) {
                item.setDescription(dto.getDescription());
            }
            if (dto.getAvailable() != null) {
                item.setAvailable(dto.getAvailable());
            }
            return item;
        }
    }

    public ItemDto toDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        return itemDto;
    }

}
