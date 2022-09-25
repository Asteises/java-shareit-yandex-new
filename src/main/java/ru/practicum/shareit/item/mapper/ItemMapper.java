package ru.practicum.shareit.item.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : 0,
                //TODO Вот так можно было бы мапить комментарии
                item.getComments().stream().map(CommentMapper::toCommentDto).collect(Collectors.toList())
        );
    }

    public static Item toItem(ItemDto itemDto, User user, ItemRequest request) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(user);
        item.setRequest(request);
        //TODO А так в обратную сторону
        item.setComments(itemDto.getComments().stream().map(CommentMapper::toComment).collect(Collectors.toList()));
        return item;
    }
}
