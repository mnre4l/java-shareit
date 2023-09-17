package ru.practicum.shareit.item.repository;

import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemRepository implements ItemRepository {
    /**
     * Хеш-таблица вида id вещи -> вещь
     */
    private final Map<Long, Item> items = new HashMap<>();
    /**
     * Хеш-таблица вида id пользователя -> список его вещей
     */
    private final Map<Long, List<Item>> usersItems = new HashMap<>();
    /**
     * Инкрементируемый id для вещей
     */
    long id;

    @Override
    public List<Item> findByOwner_IdOrderByIdAsc(long userId) {
        return usersItems.get(userId);
    }

    @Override
    public List<Item> findByAvailableTrueAndDescriptionContainingIgnoreCase(String someText) {
        return items.values().stream()
                //только доступные вещи
                .filter(Item::getAvailable)
                .filter(item -> isNameOrDescriptionContainText(someText, item))
                .collect(Collectors.toList());
    }

    @Override
    public Item save(Item item) {
        if (items.containsKey(item.getId())) {
            items.put(item.getId(), item);
            return item;
        }
        item.setId(++id);
        items.put(item.getId(), item);

        long userId = item.getOwner().getId();

        usersItems.compute(userId, (id, userItems) -> {
            if (userItems == null) {
                userItems = new ArrayList<>();
            }
            userItems.add(item);
            return userItems;
        });
        return item;
    }

    @Override
    public void delete(Item item) {
        items.remove(item.getId());
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {
        throw new NotYetImplementedException();
    }

    @Override
    public void deleteAll(Iterable<? extends Item> entities) {
        throw new NotYetImplementedException();
    }

    @Override
    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public boolean existsById(Long aLong) {
        throw new NotYetImplementedException();
    }

    @Override
    public List<Item> findAll() {
        return List.copyOf(items.values());
    }

    @Override
    public List<Item> findAll(Sort sort) {
        throw new NotYetImplementedException();
    }

    @Override
    public Page<Item> findAll(Pageable pageable) {
        throw new NotYetImplementedException();
    }

    @Override
    public List<Item> findAllById(Iterable<Long> longs) {
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
    public <S extends Item> List<S> saveAll(Iterable<S> entities) {
        throw new NotYetImplementedException();
    }

    @Override
    public void flush() {
        throw new NotYetImplementedException();
    }

    @Override
    public <S extends Item> S saveAndFlush(S entity) {
        throw new NotYetImplementedException();
    }

    @Override
    public <S extends Item> List<S> saveAllAndFlush(Iterable<S> entities) {
        throw new NotYetImplementedException();
    }

    @Override
    public void deleteAllInBatch(Iterable<Item> entities) {
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
    public Item getOne(Long aLong) {
        throw new NotYetImplementedException();
    }

    @Override
    public Item getById(Long aLong) {
        throw new NotYetImplementedException();
    }

    @Override
    public Item getReferenceById(Long aLong) {
        throw new NotYetImplementedException();
    }

    @Override
    public <S extends Item> Optional<S> findOne(Example<S> example) {
        throw new NotYetImplementedException();
    }

    @Override
    public <S extends Item> List<S> findAll(Example<S> example) {
        throw new NotYetImplementedException();
    }

    @Override
    public <S extends Item> List<S> findAll(Example<S> example, Sort sort) {
        throw new NotYetImplementedException();
    }

    @Override
    public <S extends Item> Page<S> findAll(Example<S> example, Pageable pageable) {
        throw new NotYetImplementedException();
    }

    @Override
    public <S extends Item> long count(Example<S> example) {
        throw new NotYetImplementedException();
    }

    @Override
    public <S extends Item> boolean exists(Example<S> example) {
        throw new NotYetImplementedException();
    }

    @Override
    public <S extends Item, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        throw new NotYetImplementedException();
    }

    @Override
    public void deleteAll() {
        items.clear();
    }

    /**
     * Метод предназначен для проверки содержания текста в названии или описании вещи.
     * Сейчас упрощенно, планируется доработать до :
     * -поиск по каждому слову в someText в отдельности (например, description = "дрель которая сверлит" должно будет найдено
     * по someText = "дрель сверлит" - сейчас поиск по сабстроке
     * -ограничение на минимальную длину/кол-во слов в someText и в description (исключени поиска по предлогам и т.д.)
     *
     * @param someText таргет-текст для поиска
     * @param item     вещь, для которой производится проверка
     * @return содержит ли описание и имя вещи таргет-текст
     */
    private boolean isNameOrDescriptionContainText(String someText, Item item) {
        String name = item.getName().toLowerCase();
        String description = item.getDescription().toLowerCase();

        return name.contains(someText.toLowerCase()) || description.contains(someText.toLowerCase());
    }
}
