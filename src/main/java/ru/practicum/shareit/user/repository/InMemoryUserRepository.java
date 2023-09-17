package ru.practicum.shareit.user.repository;

import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

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
    public User save(User user) {
        if (users.containsKey(user.getId())) {
            update(user);
            return user;
        }
        user.setId(++id);
        users.put(user.getId(), user);
        return user;
    }

    private void update(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public void delete(User user) {
        users.remove(user.getId());
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {
        throw new NotYetImplementedException();
    }

    @Override
    public void deleteAll(Iterable<? extends User> entities) {
        throw new NotYetImplementedException();
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public boolean existsById(Long aLong) {
        throw new NotYetImplementedException();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public List<User> findAll(Sort sort) {
        throw new NotYetImplementedException();
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        throw new NotYetImplementedException();
    }

    @Override
    public List<User> findAllById(Iterable<Long> longs) {
        throw new NotYetImplementedException();
    }

    @Override
    public long count() {
        throw new NotYetImplementedException();
    }

    @Override
    public void deleteById(Long aLong) {
        throw new NotYetImplementedException();
    }

    @Override
    public <S extends User> List<S> saveAll(Iterable<S> entities) {
        throw new NotYetImplementedException();
    }

    @Override
    public void flush() {
        throw new NotYetImplementedException();
    }

    @Override
    public <S extends User> S saveAndFlush(S entity) {
        throw new NotYetImplementedException();
    }

    @Override
    public <S extends User> List<S> saveAllAndFlush(Iterable<S> entities) {
        throw new NotYetImplementedException();
    }

    @Override
    public void deleteAllInBatch(Iterable<User> entities) {
        throw new NotYetImplementedException();
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {
        throw new NotYetImplementedException();
    }

    @Override
    public void deleteAllInBatch() {
        throw new NotYetImplementedException();
    }

    @Override
    public User getOne(Long aLong) {
        throw new NotYetImplementedException();
    }

    @Override
    public User getById(Long aLong) {
        throw new NotYetImplementedException();
    }

    @Override
    public User getReferenceById(Long aLong) {
        throw new NotYetImplementedException();
    }

    @Override
    public <S extends User> Optional<S> findOne(Example<S> example) {
        throw new NotYetImplementedException();
    }

    @Override
    public <S extends User> List<S> findAll(Example<S> example) {
        throw new NotYetImplementedException();
    }

    @Override
    public <S extends User> List<S> findAll(Example<S> example, Sort sort) {
        throw new NotYetImplementedException();
    }

    @Override
    public <S extends User> Page<S> findAll(Example<S> example, Pageable pageable) {
        throw new NotYetImplementedException();
    }

    @Override
    public <S extends User> long count(Example<S> example) {
        throw new NotYetImplementedException();
    }

    @Override
    public <S extends User> boolean exists(Example<S> example) {
        throw new NotYetImplementedException();
    }

    @Override
    public <S extends User, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        throw new NotYetImplementedException();
    }

    @Override
    public void deleteAll() {
        users.clear();
    }
}
