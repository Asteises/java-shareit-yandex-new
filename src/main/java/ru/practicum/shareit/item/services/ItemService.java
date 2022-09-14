package ru.practicum.shareit.item.services;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exceptions.ItemNotFound;
import ru.practicum.shareit.user.exceptions.UserNotFound;

import java.util.List;

@Service
public interface ItemService {

    ItemDto save(ItemDto itemDto, long userId) throws UserNotFound;

    ItemDto put(ItemDto itemDto, long itemId, long userId) throws ItemNotFound, UserNotFound;

    void delete(long itemId) throws ItemNotFound;

    List<ItemDto> findAll();

    ItemDto findById(long itemId) throws ItemNotFound;

    List<ItemDto> findAllByUserId(long userId);

    List<ItemDto> findAllByItemName(String text);
}
