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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Создает конструктор из тех полей которые нужны
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage; // Если стоит final для неинициализированного поля то конструктор нужен обязательно
    private final UserStorage userStorage;

    private long itemId = 0;

    @Override
    public ItemDto save(ItemDto itemDto, long userId) throws UserNotFound {
        User user = userStorage.findById(userId);
        if (user != null) {
            Item item = ItemMapper.toItem(itemDto, user, null);
            if (item.getAvailable() == null) {
                throw new ItemNullParametr(String.format("Available not exist - %s", item.getAvailable()));
            }
            if (item.getName() == null || item.getName().isEmpty()) {
                throw new ItemNullParametr(String.format("Name not exist - %s", item.getName()));
            }
            if (item.getDescription() == null || item.getDescription().isEmpty()) {
                throw new ItemNullParametr(String.format("Description not exist - %s", item.getDescription()));
            }
            item.setId(++itemId);
            item.setOwner(user);
            itemStorage.save(item);
            return ItemMapper.toItemDto(item);
        } else {
            throw new UserNotFound("User %s not found", userId);
        }
    }

    @Override
    public ItemDto put(ItemDto itemDto, long itemId, long userId) throws ItemNotFound, UserNotFound {
        try {
            Item item = itemStorage.findById(itemId);
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
                itemStorage.put(item, itemId);
                return ItemMapper.toItemDto(item);
            } else {
                throw new UserNotFound("User %s not found", userId);
            }
        } catch (ItemNotFound e) {
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
        try {
            return ItemMapper.toItemDto(itemStorage.findById(itemId));
        } catch (ItemNotFound e) {
            throw new ItemNotFound("Item %s not found", itemId);
        }
    }

    @Override
    public List<ItemDto> findAllByUserId(long userId) throws UserNotFound {
        try {
            return itemStorage.findAllByUserId(userId).stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        } catch (UserNotFound e) {
            throw new UserNotFound("User %s not found", userId);
        }
    }

    @Override
    public List<ItemDto> findAllByItemName(String text) {
        return itemStorage.findAllByItemName(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
