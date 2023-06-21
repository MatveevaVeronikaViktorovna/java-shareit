package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoForItemRequest;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoForResponse;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public ItemRequestDtoForResponse create(Long userId, ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);

        User requestor = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Пользователь с id {} не найден", userId);
            throw new EntityNotFoundException(String.format("Пользователь с id %d не найден", userId));
        });
        itemRequest.setRequestor(requestor);

        itemRequest.setCreated(LocalDateTime.now());
        ItemRequest newItemRequest = itemRequestRepository.save(itemRequest);
        log.info("Добавлен запрос вещи: {}", newItemRequest);
        return ItemRequestMapper.toDto(newItemRequest);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDtoForResponse> getAllByRequestor(Long userId) {
        if (!userRepository.existsById(userId)) {
            log.warn("Пользователь с id {} не найден", userId);
            throw new EntityNotFoundException(String.format("Пользователь с id %d не найден", userId));
        }
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorIdOrderByCreatedAsc(userId);
        List<ItemRequestDtoForResponse> dto = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            ItemRequestDtoForResponse itemRequestDtoForResponse = ItemRequestMapper.toDto(itemRequest);
            setItems(itemRequestDtoForResponse);
            dto.add(itemRequestDtoForResponse);
        }
        return dto;
    }

    @Transactional(readOnly = true)
    private void setItems(ItemRequestDtoForResponse dto) {
        List<ItemDtoForItemRequest> items = itemRepository.findAllByRequestIdOrderByIdAsc(dto.getId())
                .stream()
                .map(ItemMapper::toDtoForItemRequest)
                .collect(Collectors.toList());
        dto.setItems(items);
    }

}