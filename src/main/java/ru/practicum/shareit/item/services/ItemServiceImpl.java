package ru.practicum.shareit.item.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exceptions.ItemNotFound;
import ru.practicum.shareit.item.exceptions.ItemNullParametr;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositores.ItemStorage;
import ru.practicum.shareit.user.exceptions.UserNotFound;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repositoryes.UserStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Создает конструктор из тех полей которые нужны
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage; // Если стоит final для неинициализированного поля то конструктор нужен обязательно
    private final UserStorage userStorage;

    @Override
    public ItemDto save(ItemDto itemDto, long userId) throws UserNotFound {
        Optional<User> optionalUser = userStorage.findById(userId);
        if (optionalUser.isPresent()) {
            Item item = ItemMapper.toItem(itemDto, optionalUser.get(), null);
            if (item.getAvailable() == null) {
                throw new ItemNullParametr(String.format("Available not exist - %s", item.getAvailable()));
            }
            if (item.getName() == null || item.getName().isEmpty()) {
                throw new ItemNullParametr(String.format("Name not exist - %s", item.getName()));
            }
            if (item.getDescription() == null || item.getDescription().isEmpty()) {
                throw new ItemNullParametr(String.format("Description not exist - %s", item.getDescription()));
            }
            item.setOwner(optionalUser.get());
            itemStorage.save(item);
            return ItemMapper.toItemDto(item);
        } else {
            throw new UserNotFound("User %s not found", userId);
        }
    }

    @Override
    public ItemDto put(ItemDto itemDto, long itemId, long userId) throws ItemNotFound, UserNotFound {
        Optional<Item> optionalItem = itemStorage.findById(itemId);
        if (optionalItem.isPresent()) {
            Item item = optionalItem.get();
            if (item.getOwner() != null && item.getOwner().getId().equals(userId)) {
                if (itemDto.getAvailable() != null) {
                    item.setAvailable(itemDto.getAvailable());
                }
                if (itemDto.getName() != null) {
                    item.setName(itemDto.getName());
                }
                if (itemDto.getDescription() != null) {
                    item.setDescription(itemDto.getDescription());
                }
                itemStorage.save(item);
                return ItemMapper.toItemDto(item);
            } else {
                throw new UserNotFound("User %s not found", userId);
            }
        } else {
            throw new ItemNotFound("Item %s not found", itemId);
        }
    }

    @Override
    public void delete(long itemId) throws ItemNotFound {
        if (!itemStorage.findAll().removeIf(item -> item.getId() == itemId)) {
            throw new ItemNotFound("Item %s not found", itemId);
        }
    }

    @Override
    public List<ItemDto> findAll() {
        return itemStorage.findAll().stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto findById(long itemId) throws ItemNotFound {
        Optional<Item> optionalItem = itemStorage.findById(itemId);
        if (optionalItem.isPresent()) {
            return ItemMapper.toItemDto(optionalItem.get());
        } else {
            throw new ItemNotFound("Item %s not found", itemId);
        }
    }

    @Override
    public List<ItemDto> findAllByUserId(long userId) throws UserNotFound {
        Optional<User> optionalUser = userStorage.findById(userId);
        if (optionalUser.isPresent()) {
            return itemStorage.findAllByOwnerId(userId).stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        } else {
            throw new UserNotFound("User %s not found", userId);
        }
    }

    @Override
    public List<ItemDto> findAllByItemName(String text) {
        return itemStorage.findAllByName(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
