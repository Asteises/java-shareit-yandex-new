package ru.practicum.shareit.item.repositores;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.exceptions.ItemNotFound;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.exceptions.UserNotFound;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ItemDao implements ItemStorage {

    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item save(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item put(Item item, long itemId) throws ItemNotFound {
        items.replace(itemId, item);
        return item;
    }

    @Override
    public void delete(long itemId) throws ItemNotFound {
        items.remove(itemId);
    }

    @Override
    public List<Item> findAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public Item findById(long itemId) throws ItemNotFound {
        return items.get(itemId);
    }

    @Override
    public List<Item> findAllByUserId(long userId) throws ItemNotFound, UserNotFound {
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> findAllByItemName(String text) {
        List<Item> findItems = new ArrayList<>();
        String checkText = text.toLowerCase();
        if (!checkText.isEmpty()) {
            return items.values().stream()
                    .filter(item -> (item.getName().toLowerCase().contains(checkText))
                            || (item.getDescription().toLowerCase().contains(checkText)))
                    .filter(Item::getAvailable)
                    .collect(Collectors.toList());
        }
        return findItems;
    }
}
