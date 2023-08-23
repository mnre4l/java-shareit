package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * Класс представляет собой реализацию репозитория пользователей в памяти.
 */
@Repository("InMemoryUserRepository")
public class InMemoryUserRepository implements UserRepository {
    /**
     * Хеш-таблица вида id пользователя -> пользователь.
     */
    private final HashMap<Long, User> users = new HashMap<>();
    /**
     * Инкрементируемый id
     */
    private long id;

    @Override
    public void create(User user) {
        user.setId(++id);
        users.put(user.getId(), user);
    }

    @Override
    public void update(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public void delete(User user) {
        users.remove(user.getId());
    }

    @Override
    public Optional<User> get(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> getAll() {
        return List.copyOf(users.values());
    }

    @Override
    public void deleteAll() {
        users.clear();
    }
}
