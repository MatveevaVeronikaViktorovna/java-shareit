package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoForResponse;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

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
    private ItemRequestServiceImpl itemRequestService;

    @Captor
    private ArgumentCaptor<ItemRequest> itemRequestArgumentCaptor;

    @Test
    void createWhenRequestorFoundThenSavedItemRequest() {
        Long userId = 0L;
        User requestor = new User();
        requestor.setId(userId);

        ItemRequestDto requestItemRequestDto = new ItemRequestDto();

        ItemRequest expectedItemRequest = new ItemRequest();
        expectedItemRequest.setRequestor(requestor);
        ItemRequestDtoForResponse expectedItemRequestDto = ItemRequestMapper.toDto(expectedItemRequest);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(requestor));
        Mockito.when(itemRequestRepository.save(Mockito.any(ItemRequest.class))).thenReturn(expectedItemRequest);

        ItemRequestDtoForResponse itemRequest = itemRequestService.create(userId, requestItemRequestDto);

        assertEquals(expectedItemRequestDto, itemRequest);
        verify(itemRequestRepository).save(Mockito.any(ItemRequest.class));
    }

    @Test
    void createWhenRequestorNotFoundThenNotSavedItemRequest() {
        Long userId = 0L;
        ItemRequestDto requestItemRequestDto = new ItemRequestDto();
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.create(userId, requestItemRequestDto));
        verify(itemRequestRepository, Mockito.never()).save(Mockito.any(ItemRequest.class));
    }

    @Test
    void getAllByRequestorWhenRequestorFoundThenReturnedListOfItemRequest(){
        
    }


}