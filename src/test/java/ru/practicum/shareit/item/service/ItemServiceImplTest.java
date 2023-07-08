package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
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
import ru.practicum.shareit.pagination.CustomPageRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.IntPredicate;

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
    private Long ownerId;
    private User owner;
    Long requestId;
    ItemRequest request;
    private Long id;
    private Item expectedItem;
    private ItemDto expectedItemDto;
    Booking lastBooking;
    Booking nextBooking;
    private Integer from;
    private Integer size;
    Pageable page;


    @BeforeEach
    public void addItems() {
        ownerId = 0L;
        owner = new User();
        owner.setId(ownerId);

        requestId = 0L;
        request = new ItemRequest();
        request.setId(requestId);

        id = 0L;
        expectedItem = new Item();
        expectedItem.setId(id);
        expectedItem.setName("name");
        expectedItem.setDescription("description");
        expectedItem.setAvailable(true);
        expectedItem.setOwner(owner);


        lastBooking = new Booking();
        User booker = new User();
        booker.setId(1L);
        lastBooking.setBooker(booker);
        nextBooking = new Booking();
        nextBooking.setBooker(booker);

        expectedItemDto = ItemMapper.toDto(expectedItem);
        expectedItemDto.setComments(Collections.emptyList());
        expectedItemDto.setLastBooking(BookingMapper.toDtoForItem(lastBooking));
        expectedItemDto.setNextBooking(BookingMapper.toDtoForItem(nextBooking));

        from = 0;
        size = 10;
        page = CustomPageRequest.of(from, size);
    }

    @Test
    void createWhenOwnerFoundThenSavedItem() {
        Mockito.when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        Mockito.when(itemRepository.save(Mockito.any(Item.class))).thenReturn(expectedItem);

        ItemDto item = itemService.create(ownerId, expectedItemDto);

        assertEquals(expectedItemDto, item);
        verify(itemRepository).save(expectedItem);
    }

    @Test
    void createWhenOwnerNotFoundThenNotSavedItem() {
        Mockito.when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.create(ownerId, expectedItemDto));

        verify(itemRepository, Mockito.never()).save(expectedItem);
    }

    @Test
    void createWhenOwnerFoundAndRequestIdNotNullThenSavedItem() {
        expectedItem.setRequest(request);
        expectedItemDto = ItemMapper.toDto(expectedItem);
        Mockito.when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        Mockito.when(itemRequestRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(request));
        Mockito.when(itemRepository.save(Mockito.any(Item.class))).thenReturn(expectedItem);

        ItemDto item = itemService.create(ownerId, expectedItemDto);

        assertEquals(expectedItemDto, item);
        verify(itemRepository).save(expectedItem);
    }

    @Test
    void getAllByOwnerWhenInvokedThenReturnedListOfItems() {
        List<Item> expectedItems = List.of(new Item());
        List<ItemDto> expectedItemsDto = ItemMapper.toDto(expectedItems);
        expectedItemsDto.forEach(itemDto -> itemDto.setComments(Collections.emptyList()));
        Mockito.when(itemRepository.findAllByOwnerIdOrderByIdAsc(ownerId, page)).thenReturn(expectedItems);

        List<ItemDto> items = itemService.getAllByOwner(ownerId, from, size);

        assertEquals(expectedItemsDto, items);
    }

    @Test
    void getByIdWhenItemFoundAndRequestNotFromOwnerThenReturnedItem() {
        Long userId = 99L;
        Mockito.when(itemRepository.findById(id)).thenReturn(Optional.of(expectedItem));

        ItemDto item = itemService.getById(userId, id);

        assertEquals(expectedItemDto, item);
    }

    @Test
    void getByIdWhenItemFoundAndRequestFromOwnerThenReturnedItemWithLastBooking() {
        Mockito.when(itemRepository.findById(id)).thenReturn(Optional.of(expectedItem));
        Mockito.when(bookingRepository.findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(Mockito.anyLong(),
                Mockito.any(LocalDateTime.class), Mockito.any(Status.class))).thenReturn(Optional.of(lastBooking));
        Mockito.when(bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(Mockito.anyLong(),
                Mockito.any(LocalDateTime.class), Mockito.any(Status.class))).thenReturn(Optional.of(nextBooking));

        ItemDto item = itemService.getById(ownerId, id);

        assertEquals(expectedItemDto, item);
    }

    @Test
    void getByIdWhenItemNotFoundThenNotReturnedItem() {
        Mockito.when(itemRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.getById(ownerId, id));
    }

    @Test
    void updateWhenItemFoundAndOwnerCorrectThenItemUpdated() {
        Item newItem = new Item();
        newItem.setName("updatedName");
        newItem.setDescription("updated description");
        newItem.setAvailable(false);
        ItemDto newItemDto = ItemMapper.toDto(newItem);
        Mockito.when(itemRepository.findById(id)).thenReturn(Optional.of(expectedItem));

        itemService.update(ownerId, id, newItemDto);

        verify(itemRepository).save(itemArgumentCaptor.capture());
        Item savedItem = itemArgumentCaptor.getValue();
        assertEquals("updatedName", savedItem.getName());
        assertEquals("updated description", savedItem.getDescription());
        assertEquals(false, savedItem.getAvailable());
    }

    @Test
    void updateWhenItemFoundButOwnerWrongThenItemNotUpdated() {
        Long userId = 99L;
        Item newItem = new Item();
        ItemDto newItemDto = ItemMapper.toDto(newItem);
        Mockito.when(itemRepository.findById(id)).thenReturn(Optional.of(expectedItem));

        assertThrows(EntityNotFoundException.class, () -> itemService.update(userId, id, newItemDto));
        verify(itemRepository, Mockito.never()).save(Mockito.any(Item.class));
    }

    @Test
    void updateNameWhenItemFoundAndOwnerCorrectThenUpdatedOnlyName() {
        Item newItem = new Item();
        newItem.setName("updatedName");
        ItemDto newItemDto = ItemMapper.toDto(newItem);
        Mockito.when(itemRepository.findById(id)).thenReturn(Optional.of(expectedItem));

        itemService.update(ownerId, id, newItemDto);

        verify(itemRepository).save(itemArgumentCaptor.capture());
        Item savedItem = itemArgumentCaptor.getValue();
        assertEquals("updatedName", savedItem.getName());
        assertEquals("description", savedItem.getDescription());
        assertEquals(true, savedItem.getAvailable());
    }

    @Test
    void updateDescriptionWhenItemFoundAndOwnerCorrectThenUpdatedOnlyDescription() {
        Item newItem = new Item();
        newItem.setDescription("updated description");
        ItemDto newItemDto = ItemMapper.toDto(newItem);
        Mockito.when(itemRepository.findById(id)).thenReturn(Optional.of(expectedItem));

        itemService.update(ownerId, id, newItemDto);

        verify(itemRepository).save(itemArgumentCaptor.capture());
        Item savedItem = itemArgumentCaptor.getValue();
        assertEquals("name", savedItem.getName());
        assertEquals("updated description", savedItem.getDescription());
        assertEquals(true, savedItem.getAvailable());
    }

    @Test
    void updateAvailableWhenItemFoundAndOwnerCorrectThenUpdatedOnlyAvailable() {
        Item newItem = new Item();
        newItem.setAvailable(false);
        ItemDto newItemDto = ItemMapper.toDto(newItem);
        Mockito.when(itemRepository.findById(id)).thenReturn(Optional.of(expectedItem));

        itemService.update(ownerId, id, newItemDto);

        verify(itemRepository).save(itemArgumentCaptor.capture());
        Item savedItem = itemArgumentCaptor.getValue();
        assertEquals("name", savedItem.getName());
        assertEquals("description", savedItem.getDescription());
        assertEquals(false, savedItem.getAvailable());
    }

    @Test
    void updateWhenItemNotFoundThenNotUpdatedItem() {
        Item newItem = new Item();
        ItemDto newItemDto = ItemMapper.toDto(newItem);
        Mockito.when(itemRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.update(ownerId, id, newItemDto));
        verify(itemRepository, Mockito.never()).save(Mockito.any(Item.class));
    }

    @Test
    void deleteWhenInvokedThenDeleteItem() {
        itemService.delete(id);

        verify(itemRepository, Mockito.times(1)).deleteById(id);
    }

    @Test
    void searchByTextWhenInvokedThenReturnedListOfItems() {
        String text = "eSC";
        List<Item> expectedItems = List.of(expectedItem);
        List<ItemDto> expectedItemsDto = ItemMapper.toDto(expectedItems);
        Mockito.when(itemRepository.findAllAvailableByText(text, page)).thenReturn(expectedItems);

        List<ItemDto> items = itemService.searchByText(ownerId, text, from, size);

        assertEquals(expectedItemsDto, items);
    }

    @Test
    void searchByyTextWhenTextEmptyThenReturnedEmptyList() {
        String text = "";

        List<ItemDto> items = itemService.searchByText(ownerId, text, from, size);

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

        Mockito.when(bookingRepository.findAllByBookerIdAndItemIdAndEndBeforeOrderByStartDesc(Mockito.anyLong(),
                Mockito.anyLong(), Mockito.any(LocalDateTime.class))).thenReturn(List.of(new Booking()));
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
        Mockito.when(bookingRepository.findAllByBookerIdAndItemIdAndEndBeforeOrderByStartDesc(Mockito.anyLong(),
                Mockito.anyLong(), Mockito.any(LocalDateTime.class))).thenReturn(Collections.emptyList());

        assertThrows(CommentWithoutBookingException.class, () -> itemService
                .addComment(userId, itemId, requestCommentDto));
        verify(commentRepository, Mockito.never()).save(Mockito.any(Comment.class));
    }

    @Test
    void addCommentWhenBookerCorrectButAuthorNotFoundThenNotAddedComment() {
        Long userId = 0L;
        Long itemId = 0L;
        CommentDto requestCommentDto = new CommentDto();

        Mockito.when(bookingRepository.findAllByBookerIdAndItemIdAndEndBeforeOrderByStartDesc(Mockito.anyLong(),
                Mockito.anyLong(), Mockito.any(LocalDateTime.class))).thenReturn(List.of(new Booking()));
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

        Mockito.when(bookingRepository.findAllByBookerIdAndItemIdAndEndBeforeOrderByStartDesc(Mockito.anyLong(),
                Mockito.anyLong(), Mockito.any(LocalDateTime.class))).thenReturn(List.of(new Booking()));
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(author));
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.addComment(userId, itemId, requestCommentDto));
        verify(commentRepository, Mockito.never()).save(Mockito.any(Comment.class));
    }

}