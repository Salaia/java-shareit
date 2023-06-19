package ru.practicum.shareit.request.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoInput;
import ru.practicum.shareit.request.dto.ItemRequestDtoOutput;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    ItemRequestRepository itemRequestRepository;
    UserRepository userRepository;
    ItemRepository itemRepository;

    @Override
    public ItemRequestDtoOutput create(Long requesterId, ItemRequestDtoInput inputDto) {
        Optional<User> optionalUser = userRepository.findById(requesterId);
        if (optionalUser.isEmpty()) {
            throw new EntityNotFoundException("Not found: requester " + requesterId);
        }
        User requester = optionalUser.get();
        ItemRequest model = ItemRequestMapper.toModel(inputDto, requester);
        return ItemRequestMapper.toDtoOutput(itemRequestRepository.save(model));
    }

    @Override
    public List<ItemRequestDtoOutput> findAllForRequester(Long requesterId, Integer from, Integer size) {
        Optional<User> optionalUser = userRepository.findById(requesterId);
        if (optionalUser.isEmpty()) {
            throw new EntityNotFoundException("Not found: requester " + requesterId);
        }

        int page = from / size;
        Pageable params = PageRequest.of(page, size, Sort.by("created"));
        List<ItemRequest> models = itemRequestRepository.findByRequesterId(requesterId, params);
/*
        if(models == null) {
            models = new ArrayList<>();
        }
*/
        List<ItemRequestDtoOutput> result = ItemRequestMapper.toDtoList(models);
        addItemsToRequests(result);
        return result;
    }

    @Override
    public List<ItemRequestDtoOutput> findAllFromOthers(Long requesterId, Integer from, Integer size) {
        List<ItemRequestDtoOutput> result;
/*
        if (from == null || size == null) {
            Pageable params = PageRequest.of(0, 10, Sort.by("created"));
            List<ItemRequest> models = itemRequestRepository.findAllFromOthersWithoutParams(requesterId, params).toList();
            result = ItemRequestMapper.toDtoList(models);
        } else {
*/
        Pageable params = PageRequest.of(from, size, Sort.by("created"));
        List<ItemRequest> models = itemRequestRepository.findAllFromOthers(requesterId, params);
        result = ItemRequestMapper.toDtoList(models);
        addItemsToRequests(result);
        // }
        return result;
    }

    @Override
    public ItemRequestDtoOutput findById(Long userId, Long requestId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new EntityNotFoundException("Not found: user " + userId);
        }
        Optional<ItemRequest> requestOptional = itemRequestRepository.findById(requestId);
        if (requestOptional.isEmpty()) {
            throw new EntityNotFoundException("Not found: request " + requestId);
        }
        ItemRequest request = requestOptional.get();
        ItemRequestDtoOutput dtoOutput = ItemRequestMapper.toDtoOutput(request);
        List<Long> id = new ArrayList<>();
        id.add(dtoOutput.getId());
        List<Item> items = itemRepository.findByRequestIdList(id);
        dtoOutput.setItems(ItemMapper.itemDtoList(items));
        return dtoOutput;
    }

    private void addItemsToRequests(List<ItemRequestDtoOutput> requests) {
        List<Long> requestsIds = requests.stream().map(ItemRequestDtoOutput::getId)
                .collect(Collectors.toList());
        List<Item> allItems = itemRepository.findByRequestIdList(requestsIds);
        for (ItemRequestDtoOutput dto : requests) {
            List<Item> itemsForDto = allItems.stream()
                    .filter(item -> item.getRequest().getId().equals(dto.getId()))
                    .collect(Collectors.toList());
            if (!itemsForDto.isEmpty()) {
                dto.setItems(ItemMapper.itemDtoList(itemsForDto));
            } else {
                dto.setItems(new ArrayList<>());
            }
        }
    }
}
