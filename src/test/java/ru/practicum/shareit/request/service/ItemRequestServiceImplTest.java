package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.pagination.CustomPageRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoForResponse;
import ru.practicum.shareit.request.dto.ItemRequestDtoMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    private ItemRequestDtoMapper mapper = Mappers.getMapper(ItemRequestDtoMapper.class);
    private Long userId;
    private User requestor;
    private ItemRequestDto requestItemRequestDto;
    private Long id;
    private ItemRequest expectedItemRequest;
    private ItemRequestDtoForResponse expectedItemRequestDto;
    private Integer from = 0;
    private Integer size = 10;
    private Pageable page = CustomPageRequest.of(from, size);

    @BeforeEach
    public void addItemRequests() {
        userId = 0L;
        requestor = new User();
        requestor.setId(userId);

        requestItemRequestDto = new ItemRequestDto();

        id = 0L;
        expectedItemRequest = new ItemRequest();
        expectedItemRequest.setId(id);
        expectedItemRequest.setRequestor(requestor);

        expectedItemRequestDto = mapper.itemRequestToDto(expectedItemRequest);

        from = 0;
        size = 10;
        page = CustomPageRequest.of(from, size);
    }

    @Test
    void createWhenRequestorFoundThenSavedItemRequest() {
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
    void getAllByRequestorWhenRequestorFoundThenReturnedListOfItemRequest() {
        List<ItemRequest> expectedItemRequests = List.of(new ItemRequest());
        List<ItemRequestDtoForResponse> expectedItemRequestsDto = mapper.itemRequestToDto(expectedItemRequests);
        expectedItemRequestsDto.forEach(itemRequestDtoForResponse -> itemRequestDtoForResponse
                .setItems(Collections.emptyList()));
        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId))
                .thenReturn(expectedItemRequests);

        List<ItemRequestDtoForResponse> itemRequests = itemRequestService.getAllByRequestor(userId);

        assertEquals(expectedItemRequestsDto, itemRequests);
    }

    @Test
    void getAllByRequestorWhenRequestorNotFoundThenNotReturnedListOfItemRequest() {
        Mockito.when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.getAllByRequestor(userId));
    }

    @Test
    void getByIdWhenRequestorFoundAndItemRequestFoundThenReturnedItemRequest() {
        expectedItemRequestDto.setItems(Collections.emptyList());
        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(itemRequestRepository.findById(id)).thenReturn(Optional.of(expectedItemRequest));

        ItemRequestDtoForResponse itemRequest = itemRequestService.getById(userId, id);

        assertEquals(expectedItemRequestDto, itemRequest);
    }

    @Test
    void getByIdWhenRequestorNotFoundThenNotReturnedItemRequest() {
        Mockito.when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.getById(userId, id));
    }

    @Test
    void getByIdWhenRequestorFoundButItemRequestNotFoundThenNotReturnedItemRequest() {
        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(itemRequestRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.getById(userId, id));
    }

    @Test
    void getAllWhenInvokedThenReturnedListOfItemRequest() {
        List<ItemRequest> expectedItemRequests = List.of(new ItemRequest());
        List<ItemRequestDtoForResponse> expectedItemRequestsDto = mapper.itemRequestToDto(expectedItemRequests);
        expectedItemRequestsDto.forEach(itemRequestDtoForResponse -> itemRequestDtoForResponse
                .setItems(Collections.emptyList()));
        Mockito.when(itemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(userId, page))
                .thenReturn(expectedItemRequests);

        List<ItemRequestDtoForResponse> itemRequests = itemRequestService.getAll(userId, from, size);

        assertEquals(expectedItemRequestsDto, itemRequests);
    }

}