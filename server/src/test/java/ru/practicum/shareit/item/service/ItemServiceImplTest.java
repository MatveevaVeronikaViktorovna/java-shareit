package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.CommentWithoutBookingException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.*;
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
    private final BookingDtoMapper mapper = Mappers.getMapper(BookingDtoMapper.class);
    private final CommentDtoMapper commentDtoMapper = Mappers.getMapper(CommentDtoMapper.class);
    private final ItemDtoMapper itemDtoMapper = Mappers.getMapper(ItemDtoMapper.class);
    private Long ownerId;
    private User owner;
    Long authorId;
    User author;
    Long requestId;
    private ItemRequest request;
    private Long id;
    private Item expectedItem;
    private ItemDto requestItemDto;
    private ItemDto expectedItemDto;
    private ItemDto expectedItemDtoWithComments;
    private ItemDto expectedItemDtoWithBookings;
    private Booking lastBooking;
    private Booking nextBooking;
    private Long itemId;
    private Item item;
    private CommentDto requestCommentDto;
    private Comment expectedComment;
    private CommentDto expectedCommentDto;
    private Integer from;
    private Integer size;
    Pageable page;


    @BeforeEach
    public void addItems() {
        ownerId = 0L;
        owner = new User();
        owner.setId(ownerId);

        Long bookerId = 1L;
        User booker = new User();
        booker.setId(bookerId);

        authorId = 2L;
        author = new User();
        author.setId(authorId);

        requestId = 0L;
        request = new ItemRequest();
        request.setId(requestId);

        requestItemDto = new ItemDto();
        requestItemDto.setName("name");
        requestItemDto.setDescription("description");
        requestItemDto.setAvailable(true);

        id = 0L;
        expectedItem = new Item();
        expectedItem.setId(id);
        expectedItem.setName("name");
        expectedItem.setDescription("description");
        expectedItem.setAvailable(true);
        expectedItem.setOwner(owner);

        expectedItemDto = itemDtoMapper.itemToDto(expectedItem);

        expectedItemDtoWithComments = itemDtoMapper.itemToDto(expectedItem);
        expectedItemDtoWithComments.setComments(Collections.emptyList());

        lastBooking = new Booking();
        lastBooking.setBooker(booker);

        nextBooking = new Booking();
        nextBooking.setBooker(booker);

        expectedItemDtoWithBookings = itemDtoMapper.itemToDto(expectedItem);
        expectedItemDtoWithBookings.setComments(Collections.emptyList());
        expectedItemDtoWithBookings.setLastBooking(mapper.bookingToDtoForItem(lastBooking));
        expectedItemDtoWithBookings.setNextBooking(mapper.bookingToDtoForItem(nextBooking));

        itemId = 1L;
        item = new Item();
        item.setId(itemId);

        requestCommentDto = new CommentDto();

        expectedComment = new Comment();
        expectedComment.setAuthor(author);

        expectedCommentDto = commentDtoMapper.commentToDto(expectedComment);

        from = 0;
        size = 10;
        page = CustomPageRequest.of(from, size);
    }

    @Test
    void createWhenOwnerFoundThenSavedItem() {
        Mockito.when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        Mockito.when(itemRepository.save(Mockito.any(Item.class))).thenReturn(expectedItem);

        ItemDto item = itemService.create(ownerId, requestItemDto);

        assertEquals(expectedItemDto, item);
        verify(itemRepository).save(Mockito.any(Item.class));
    }

    @Test
    void createWhenOwnerNotFoundThenNotSavedItem() {
        Mockito.when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.create(ownerId, requestItemDto));

        verify(itemRepository, Mockito.never()).save(Mockito.any(Item.class));
    }

    @Test
    void createWhenOwnerFoundAndRequestIdNotNullThenSavedItem() {
        requestItemDto.setRequestId(requestId);
        expectedItem.setRequest(request);
        expectedItemDto = itemDtoMapper.itemToDto(expectedItem);
        Mockito.when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        Mockito.when(itemRequestRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(request));
        Mockito.when(itemRepository.save(Mockito.any(Item.class))).thenReturn(expectedItem);

        ItemDto item = itemService.create(ownerId, requestItemDto);

        assertEquals(expectedItemDto, item);
        verify(itemRepository).save(Mockito.any(Item.class));
    }

    @Test
    void getAllByOwnerWhenInvokedThenReturnedListOfItems() {
        List<Item> expectedItems = List.of(new Item());
        List<ItemDto> expectedItemsDto = itemDtoMapper.itemToDto(expectedItems);
        expectedItemsDto.forEach(itemDto -> itemDto.setComments(Collections.emptyList()));
        Mockito.when(itemRepository.findAllByOwnerIdOrderByIdAsc(ownerId, page)).thenReturn(expectedItems);

        List<ItemDto> items = itemService.getAllByOwner(ownerId, from, size);

        assertEquals(expectedItemsDto, items);
    }

    @Test
    void getByIdWhenItemFoundAndRequestNotFromOwnerThenReturnedItemWithoutLastBooking() {
        Long userId = 99L;
        Mockito.when(itemRepository.findById(id)).thenReturn(Optional.of(expectedItem));

        ItemDto item = itemService.getById(userId, id);

        assertEquals(expectedItemDtoWithComments, item);
    }

    @Test
    void getByIdWhenItemFoundAndRequestFromOwnerThenReturnedItemWithLastBooking() {
        Mockito.when(itemRepository.findById(id)).thenReturn(Optional.of(expectedItem));
        Mockito.when(bookingRepository.findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(Mockito.anyLong(),
                Mockito.any(LocalDateTime.class), Mockito.any(Status.class))).thenReturn(Optional.of(lastBooking));
        Mockito.when(bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(Mockito.anyLong(),
                Mockito.any(LocalDateTime.class), Mockito.any(Status.class))).thenReturn(Optional.of(nextBooking));

        ItemDto item = itemService.getById(ownerId, id);

        assertEquals(expectedItemDtoWithBookings, item);
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
        ItemDto newItemDto = itemDtoMapper.itemToDto(newItem);
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
        ItemDto newItemDto = itemDtoMapper.itemToDto(newItem);
        Mockito.when(itemRepository.findById(id)).thenReturn(Optional.of(expectedItem));

        assertThrows(EntityNotFoundException.class, () -> itemService.update(userId, id, newItemDto));
        verify(itemRepository, Mockito.never()).save(Mockito.any(Item.class));
    }

    @Test
    void updateNameWhenItemFoundAndOwnerCorrectThenUpdatedOnlyName() {
        Item newItem = new Item();
        newItem.setName("updatedName");
        ItemDto newItemDto = itemDtoMapper.itemToDto(newItem);
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
        ItemDto newItemDto = itemDtoMapper.itemToDto(newItem);
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
        ItemDto newItemDto = itemDtoMapper.itemToDto(newItem);
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
        ItemDto newItemDto = itemDtoMapper.itemToDto(newItem);
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
        List<ItemDto> expectedItemsDto = itemDtoMapper.itemToDto(expectedItems);
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
    void addCommentWhenAuthorBookedThisItemAndAuthorFoundAndItemFoundThenAddedComment() {
        Mockito.when(bookingRepository.findAllByBookerIdAndItemIdAndEndBeforeOrderByStartDesc(Mockito.anyLong(),
                Mockito.anyLong(), Mockito.any(LocalDateTime.class))).thenReturn(List.of(new Booking()));
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(author));
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(commentRepository.save(Mockito.any(Comment.class))).thenReturn(expectedComment);

        CommentDto comment = itemService.addComment(authorId, itemId, requestCommentDto);

        assertEquals(expectedCommentDto, comment);
        verify(commentRepository).save(Mockito.any(Comment.class));
    }

    @Test
    void addCommentWhenAuthorNotBookedThisItemThenNotAddedComment() {
        Mockito.when(bookingRepository.findAllByBookerIdAndItemIdAndEndBeforeOrderByStartDesc(Mockito.anyLong(),
                Mockito.anyLong(), Mockito.any(LocalDateTime.class))).thenReturn(Collections.emptyList());

        assertThrows(CommentWithoutBookingException.class, () -> itemService
                .addComment(authorId, itemId, requestCommentDto));
        verify(commentRepository, Mockito.never()).save(Mockito.any(Comment.class));
    }

    @Test
    void addCommentWhenAuthorBookedThisItemButAuthorNotFoundThenNotAddedComment() {
        Mockito.when(bookingRepository.findAllByBookerIdAndItemIdAndEndBeforeOrderByStartDesc(Mockito.anyLong(),
                Mockito.anyLong(), Mockito.any(LocalDateTime.class))).thenReturn(List.of(new Booking()));
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.addComment(authorId, itemId, requestCommentDto));
        verify(commentRepository, Mockito.never()).save(Mockito.any(Comment.class));
    }

    @Test
    void addCommentWhenAuthorBookedThisItemAndAuthorFoundButItemNotFoundThenNotAddedComment() {
        Mockito.when(bookingRepository.findAllByBookerIdAndItemIdAndEndBeforeOrderByStartDesc(Mockito.anyLong(),
                Mockito.anyLong(), Mockito.any(LocalDateTime.class))).thenReturn(List.of(new Booking()));
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(author));
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.addComment(authorId, itemId, requestCommentDto));
        verify(commentRepository, Mockito.never()).save(Mockito.any(Comment.class));
    }

}