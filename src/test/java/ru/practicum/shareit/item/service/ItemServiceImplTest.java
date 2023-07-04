package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.CommentWithoutBookingException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
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

        assertThrows(EntityNotFoundException.class, () -> itemService.create(id, expectedItemDto));

        verify(itemRepository, Mockito.never()).save(expectedItem);
    }

    @Test
    void createWhenOwnerFoundAndRequestIdNotNullThenSavedItem() {
        Long id = 0L;
        User owner = new User();
        Long requestId = 0L;
        ItemRequest request = new ItemRequest();
        request.setId(requestId);
        Item expectedItem = new Item();
        expectedItem.setOwner(owner);
        expectedItem.setRequest(request);
        ItemDto expectedItemDto = ItemMapper.toDto(expectedItem);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(owner));
        Mockito.when(itemRequestRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(request));
        Mockito.when(itemRepository.save(Mockito.any(Item.class))).thenReturn(expectedItem);

        ItemDto item = itemService.create(id, expectedItemDto);

        assertEquals(expectedItemDto, item);
        verify(itemRepository).save(expectedItem);
    }

    @Test
    void getAllByOwnerWhenInvokedThenReturnedListOfItems() {
        Long userId = 0L;
        Integer from = 0;
        Integer size = 10;
        Pageable page = PageRequest.of(from / size, size);
        List<Item> expectedItems = List.of(new Item());
        List<ItemDto> expectedItemsDto = ItemMapper.toDto(expectedItems);
        expectedItemsDto.forEach(itemDto -> itemDto.setComments(Collections.emptyList()));
        Mockito.when(itemRepository.findAllByOwnerIdOrderByIdAsc(userId, page)).thenReturn(expectedItems);

        List<ItemDto> items = itemService.getAllByOwner(userId, from, size);

        assertEquals(expectedItemsDto, items);
    }

    @Test
    void getByIdWhenItemFoundAndRequestNotFromOwnerThenReturnedItem() {
        Long id = 0L;
        Long userId = 0L;
        Long ownerId = 1L;
        User owner = new User();
        owner.setId(ownerId);
        Item expectedItem = new Item();
        expectedItem.setOwner(owner);
        ItemDto expectedItemDto = ItemMapper.toDto(expectedItem);
        expectedItemDto.setComments(Collections.emptyList());
        Mockito.when(itemRepository.findById(id)).thenReturn(Optional.of(expectedItem));

        ItemDto item = itemService.getById(userId, id);

        assertEquals(expectedItemDto, item);
    }

    @Test
    void getByIdWhenItemNotFoundThenNotReturnedItem() {
        Long id = 0L;
        Long userId = 0L;
        Mockito.when(itemRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.getById(userId, id));
    }

    @Test
    void updateWhenItemFoundAndOwnerCorrectThenItemUpdated() {
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
    void updateWhenItemFoundButOwnerWrongThenItemNotUpdated() {
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

        assertThrows(EntityNotFoundException.class, () -> itemService.update(userId, id, newItemDto));
        verify(itemRepository, Mockito.never()).save(Mockito.any(Item.class));
    }

    @Test
    void updateNameWhenItemFoundAndOwnerCorrectThenUpdatedOnlyName() {
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
    void updateDescriptionWhenItemFoundAndOwnerCorrectThenUpdatedOnlyDescription() {
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
    void updateAvailableWhenItemFoundAndOwnerCorrectThenUpdatedOnlyAvailable() {
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

    @Test
    void updateWhenOwnerNotFoundThenNotUpdatedItem() {
        Long userId = 0L;
        User owner = new User();
        owner.setId(userId);

        Long id = 0L;
        Item oldItem = new Item();
        oldItem.setOwner(owner);

        Item newItem = new Item();
        ItemDto newItemDto = ItemMapper.toDto(newItem);
        Mockito.when(itemRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.update(userId, id, newItemDto));
        verify(itemRepository, Mockito.never()).save(Mockito.any(Item.class));
    }

    @Test
    void deleteWhenInvokedThenDeleteItem() {
        Long id = 0L;
        itemService.delete(id);
        verify(itemRepository, Mockito.times(1)).deleteById(id);
    }

    @Test
    void searchByTextWhenInvokedThenReturnedListOfItems() {
        Long userId = 0L;
        Long id = 0L;
        Item item = new Item();
        item.setName("name");
        item.setDescription("description");
        String text = "eSC";
        Integer from = 0;
        Integer size = 10;
        Pageable page = PageRequest.of(from / size, size);
        List<Item> expectedItems = List.of(item);
        List<ItemDto> expectedItemsDto = ItemMapper.toDto(expectedItems);
        Mockito.when(itemRepository.findAllAvailableByText(text, page)).thenReturn(expectedItems);

        List<ItemDto> items = itemService.searchByText(userId, text, from, size);

        assertEquals(expectedItemsDto, items);
    }

    @Test
    void searchByyTextWhenTextEmptyThenReturnedListOfItems() {
        Long userId = 0L;
        String text = "";
        Integer from = 0;
        Integer size = 10;

        List<ItemDto> items = itemService.searchByText(userId, text, from, size);

        assertEquals(Collections.emptyList(), items);
    }

    @Test
    void addCommentWhenBookerCorrectAndAuthorFoundAndItemFoundThenAddedComment() {
        Long userId = 0L;
        User author = new User();
        author.setId(userId);

        Long itemId = 0L;
        Item item = new Item();
        item.setId(itemId);

        CommentDto requestCommentDto = new CommentDto();

        Comment expectedComment = new Comment();
        expectedComment.setAuthor(author);
        CommentDto expectedCommentDto = CommentMapper.toDto(expectedComment);

        Mockito.when(bookingRepository.findAllByBookerIdAndItemIdAndEndBeforeOrderByStartDesc(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(LocalDateTime.class))).thenReturn(List.of(new Booking()));
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(author));
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(commentRepository.save(Mockito.any(Comment.class))).thenReturn(expectedComment);

        CommentDto comment = itemService.addComment(userId, itemId, requestCommentDto);

        assertEquals(expectedCommentDto, comment);
        verify(commentRepository).save(Mockito.any(Comment.class));
    }

    @Test
    void addCommentWhenBookerWrongThenNotAddedComment() {
        Long userId = 0L;
        Long itemId = 0L;
        CommentDto requestCommentDto = new CommentDto();
        Mockito.when(bookingRepository.findAllByBookerIdAndItemIdAndEndBeforeOrderByStartDesc(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(LocalDateTime.class))).thenReturn(Collections.emptyList());

        assertThrows(CommentWithoutBookingException.class, () -> itemService.addComment(userId, itemId, requestCommentDto));
        verify(commentRepository, Mockito.never()).save(Mockito.any(Comment.class));
    }

    @Test
    void addCommentWhenBookerCorrectButAuthorNotFoundThenNotAddedComment() {
        Long userId = 0L;
        Long itemId = 0L;
        CommentDto requestCommentDto = new CommentDto();

        Mockito.when(bookingRepository.findAllByBookerIdAndItemIdAndEndBeforeOrderByStartDesc(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(LocalDateTime.class))).thenReturn(List.of(new Booking()));
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.addComment(userId, itemId, requestCommentDto));
        verify(commentRepository, Mockito.never()).save(Mockito.any(Comment.class));
    }


    @Test
    void addCommentWhenBookerCorrectAndAuthorFoundButItemNotFoundThenNotAddedComment() {
        Long userId = 0L;
        User author = new User();
        Long itemId = 0L;
        CommentDto requestCommentDto = new CommentDto();

        Mockito.when(bookingRepository.findAllByBookerIdAndItemIdAndEndBeforeOrderByStartDesc(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(LocalDateTime.class))).thenReturn(List.of(new Booking()));
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(author));
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.addComment(userId, itemId, requestCommentDto));
        verify(commentRepository, Mockito.never()).save(Mockito.any(Comment.class));
    }

}