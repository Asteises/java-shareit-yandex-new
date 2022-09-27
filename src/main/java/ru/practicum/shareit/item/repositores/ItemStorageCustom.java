package ru.practicum.shareit.item.repositores;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorageCustom {

    List<Item> findByNameAndDescription(String name, String description);
}
