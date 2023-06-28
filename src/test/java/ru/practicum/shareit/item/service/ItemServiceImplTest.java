package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Captor
    private ArgumentCaptor<Item> itemArgumentCaptor;

    @Test
    void createWhenOwnerFoundThenSavedItem() {
        Long id = 0L;
        User owner = new User();
        Item expectedItem = new Item();
        expectedItem.setOwner(owner);
        ItemDto expectedItemDto = ItemMapper.toDto(expectedItem);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(owner));
        Mockito.when(itemRepository.save(Mockito.any(Item.class))).thenReturn(expectedItem);

        ItemDto item = itemService.create(id, expectedItemDto);

        assertEquals(expectedItemDto, item);
        verify(itemRepository).save(expectedItem);
    }

    @Test
    void createWhenOwnerNotFoundThenNotSavedItem() {
        Long id = 0L;
        Item expectedItem = new Item();
        ItemDto expectedItemDto = ItemMapper.toDto(expectedItem);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.create(id,expectedItemDto));

        verify(itemRepository, Mockito.never()).save(expectedItem);
    }

    @Test
    void updateWhenItemFoundAndOwnerCorrectThenItemUpdated(){
        Long userId = 0L;
        User owner = new User();
        owner.setId(userId);

        Long id = 0L;
        Item oldItem = new Item();
        oldItem.setId(id);
        oldItem.setName("name");
        oldItem.setDescription("description");
        oldItem.setAvailable(true);
        oldItem.setOwner(owner);

        Item newItem = new Item();
        newItem.setName("updatedName");
        newItem.setDescription("updated description");
        newItem.setAvailable(false);
        ItemDto newItemDto = ItemMapper.toDto(newItem);
        Mockito.when(itemRepository.findById(id)).thenReturn(Optional.of(oldItem));

        itemService.update(userId, id, newItemDto);

        verify(itemRepository).save(itemArgumentCaptor.capture());
        Item savedItem = itemArgumentCaptor.getValue();
        assertEquals("updatedName", savedItem.getName());
        assertEquals("updated description", savedItem.getDescription());
        assertEquals(false, savedItem.getAvailable());
    }

    @Test
    void updateWhenItemFoundButOwnerWrongThenItemNotUpdated(){
        Long userId = 0L;
        Long ownerId = 1L;
        User owner = new User();
        owner.setId(ownerId);

        Long id = 0L;
        Item oldItem = new Item();
        oldItem.setOwner(owner);

        Item newItem = new Item();
        ItemDto newItemDto = ItemMapper.toDto(newItem);
        Mockito.when(itemRepository.findById(id)).thenReturn(Optional.of(oldItem));

        assertThrows(EntityNotFoundException.class, () -> itemService.update(userId, id,newItemDto));
        verify(itemRepository, Mockito.never()).save(Mockito.any(Item.class));
    }

    @Test
    void updateNameWhenItemFoundAndOwnerCorrectThenUpdatedOnlyName(){
        Long userId = 0L;
        User owner = new User();
        owner.setId(userId);

        Long id = 0L;
        Item oldItem = new Item();
        oldItem.setId(id);
        oldItem.setName("name");
        oldItem.setDescription("description");
        oldItem.setAvailable(true);
        oldItem.setOwner(owner);

        Item newItem = new Item();
        newItem.setName("updatedName");
        ItemDto newItemDto = ItemMapper.toDto(newItem);
        Mockito.when(itemRepository.findById(id)).thenReturn(Optional.of(oldItem));

        itemService.update(userId, id, newItemDto);

        verify(itemRepository).save(itemArgumentCaptor.capture());
        Item savedItem = itemArgumentCaptor.getValue();
        assertEquals("updatedName", savedItem.getName());
        assertEquals("description", savedItem.getDescription());
        assertEquals(true, savedItem.getAvailable());
    }

    @Test
    void updateDescriptionWhenItemFoundAndOwnerCorrectThenUpdatedOnlyDescription(){
        Long userId = 0L;
        User owner = new User();
        owner.setId(userId);

        Long id = 0L;
        Item oldItem = new Item();
        oldItem.setId(id);
        oldItem.setName("name");
        oldItem.setDescription("description");
        oldItem.setAvailable(true);
        oldItem.setOwner(owner);

        Item newItem = new Item();
        newItem.setDescription("updated description");
        ItemDto newItemDto = ItemMapper.toDto(newItem);
        Mockito.when(itemRepository.findById(id)).thenReturn(Optional.of(oldItem));

        itemService.update(userId, id, newItemDto);

        verify(itemRepository).save(itemArgumentCaptor.capture());
        Item savedItem = itemArgumentCaptor.getValue();
        assertEquals("name", savedItem.getName());
        assertEquals("updated description", savedItem.getDescription());
        assertEquals(true, savedItem.getAvailable());
    }

    @Test
    void updateAvailableWhenItemFoundAndOwnerCorrectThenUpdatedOnlyAvailable(){
        Long userId = 0L;
        User owner = new User();
        owner.setId(userId);

        Long id = 0L;
        Item oldItem = new Item();
        oldItem.setId(id);
        oldItem.setName("name");
        oldItem.setDescription("description");
        oldItem.setAvailable(true);
        oldItem.setOwner(owner);

        Item newItem = new Item();
        newItem.setAvailable(false);
        ItemDto newItemDto = ItemMapper.toDto(newItem);
        Mockito.when(itemRepository.findById(id)).thenReturn(Optional.of(oldItem));

        itemService.update(userId, id, newItemDto);

        verify(itemRepository).save(itemArgumentCaptor.capture());
        Item savedItem = itemArgumentCaptor.getValue();
        assertEquals("name", savedItem.getName());
        assertEquals("description", savedItem.getDescription());
        assertEquals(false, savedItem.getAvailable());
    }

    

}