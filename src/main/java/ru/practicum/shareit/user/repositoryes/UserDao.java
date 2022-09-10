package ru.practicum.shareit.user.repositoryes;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.exceptions.UserNotFound;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class UserDao implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();


    @Override
    public User save(User user) {
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public User put(User user, long userId) throws UserNotFound {
        return users.replace(userId, user);
    }

    @Override
    public void delete(long userId) throws UserNotFound {
        users.remove(userId);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User findById(long userId) throws UserNotFound {
        return users.get(userId);
    }
}
