package ru.practicum.shareit.item.repositores;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.exceptions.ItemNotFound;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemStorage {

    Item save(Item item);

    Item put(Item item, long itemId) throws ItemNotFound;

    void delete(long itemId) throws ItemNotFound;

    List<Item> findAll();

    Item findById(long itemId) throws ItemNotFound;

    List<Item> findAllByUserId(long userId) throws ItemNotFound;

    List<Item> findAllByItemName(String text);
}
