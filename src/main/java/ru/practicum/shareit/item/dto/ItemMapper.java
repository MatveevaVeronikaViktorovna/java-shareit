package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;

@Data
public class ItemMapper {

    public static Item convertDtoForCreateToItem(ItemDtoForCreate dto) {
        if (dto == null) return null;
        else {
            Item item = new Item();
            item.setName(dto.getName());
            item.setDescription(dto.getDescription());
            item.setAvailable(dto.getAvailable());
            return item;
        }
    }

    public static Item convertDtoForUpdateToItem(ItemDtoForUpdate dto) {
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

    public static ItemDtoForCreate convertItemToDto(Item item) {
        ItemDtoForCreate itemDtoForCreate = new ItemDtoForCreate();
        itemDtoForCreate.setId(item.getId());
        itemDtoForCreate.setName(item.getName());
        itemDtoForCreate.setDescription(item.getDescription());
        itemDtoForCreate.setAvailable(item.getAvailable());
        return itemDtoForCreate;
    }

}
