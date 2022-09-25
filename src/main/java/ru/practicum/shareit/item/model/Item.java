package ru.practicum.shareit.item.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "ITEMS")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "DESCRIPTION", insertable = false, updatable = false)
    private String description;

    @Column(name = "DESCRIPTION")
    private Boolean available;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "OWNER_ID", nullable = false, insertable = false, updatable = false)
    private User owner;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "REQUEST_ID")
    private ItemRequest request;

    //TODO Задача - вывести все отзывы для Item. Варианты - связать таблицы с помощью JPA или хранить отзывы внутри Item.
    // Как связать таблицы с помощью JPA и передать их в сущность?
    // Или мы же можем хранить все отзывы прямо в Item? Так бы при появлении отзыва можно было бы просто сохранить его...
    // Или когда мы объявляем @Entity то связываем сущность напрямую с таблицей БД
    // и не можем иметь в ней дополнительные поля, которые не задействованы в таблице?
//    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
//    @JoinTable(name = "COMMENTS",
//            joinColumns = @JoinColumn(name = "ITEM_ID"),
//            inverseJoinColumns = @JoinColumn(name = "ID"))
    private List<Comment> comments = new ArrayList<>();
}
