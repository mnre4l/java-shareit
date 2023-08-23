package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
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
    public List<Item> getUserItemsByUserId(long userId) {
        return usersItems.get(userId);
    }

    @Override
    public List<Item> findItemsBy(String someText) {
        //наверное, по хорошему нужно делать валидацию на someText, но тесты требуют именно пустой лист
        if (someText.isBlank()) return Collections.emptyList();
        return items.values().stream()
                //только доступные вещи
                .filter(item -> item.getAvailable() == true)
                .filter(item -> isNameOrDescriptionContainText(someText, item))
                .collect(Collectors.toList());
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

    @Override
    public void create(Item item) {
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
    }

    @Override
    public void update(Item item) {
        items.put(item.getId(), item);
    }

    @Override
    public void delete(Item item) {
        items.remove(item.getId());
    }

    @Override
    public Optional<Item> get(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<Item> getAll() {
        return List.copyOf(items.values());
    }

    @Override
    public void deleteAll() {
        items.clear();
    }
}
