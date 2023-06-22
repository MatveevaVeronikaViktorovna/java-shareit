package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    @Override
    public ItemRequestDtoForResponse getById(Long userId, Long id) {
        if (!userRepository.existsById(userId)) {
            log.warn("Пользователь с id {} не найден", userId);
            throw new EntityNotFoundException(String.format("Пользователь с id %d не найден", userId));
        }
        ItemRequest request = itemRequestRepository.findById(id).orElseThrow(() -> {
            log.warn("Запрос вещи с id {} не найден", id);
            throw new EntityNotFoundException(String.format("Запрос вещи с id %d не найден", id));
        });
        ItemRequestDtoForResponse requestDto = ItemRequestMapper.toDto(request);
        setItems(requestDto);
        return requestDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDtoForResponse> getAll(Long userId, Integer from, Integer size) {
        List<ItemRequestDtoForResponse> allDto = new ArrayList<>();
        Sort sortByCreated = Sort.by(Sort.Direction.ASC, "created");
        Pageable page = PageRequest.of(from, size, sortByCreated);
        Page<ItemRequest> itemRequestPage = itemRequestRepository.findAll(page);
        itemRequestPage.getContent().forEach(itemRequest -> {
            ItemRequestDtoForResponse dto = ItemRequestMapper.toDto(itemRequest);
            setItems(dto);
            allDto.add(dto);
        });
        return allDto;
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
