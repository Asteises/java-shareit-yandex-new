package ru.practicum.shareit.item.repositores;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemStorage extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerId(Long userId);

    @Query(value = "select * from ITEMS I " +
            "where lower(I.NAME) = ?1 OR lower(I.DESCRIPTION) = ?2"
            , nativeQuery = true)
    List<Item> findAllByNameAndDescriptionLowerCase(String name, String description);

    List<Item> findAllByNameContains(String name);
}
