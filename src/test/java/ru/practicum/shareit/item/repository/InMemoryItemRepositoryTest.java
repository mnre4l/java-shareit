package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.InMemoryUserRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InMemoryItemRepositoryTest {
    private UserRepository userRepository;
    private ItemRepository itemRepository;
    private User firstUser;
    private User secondUser;
    private Item firstItem;
    private Item secondItem;

    @BeforeEach
    void init() {
        firstItem = initFirstItemWithoutOwner();
        secondItem = initSecondItemWithoutOwner();
        firstUser = initFirstUser();
        secondUser = initSecondUser();

        itemRepository = new InMemoryItemRepository();
        userRepository = new InMemoryUserRepository();

        userRepository.save(firstUser);
        userRepository.save(secondUser);
    }

    @Test
    void shouldCreateItems() {
        firstItem.setOwner(firstUser);

        itemRepository.save(firstItem);
        /*
        проверка что установился id
         */
        assertEquals(1, firstItem.getId());
    }

    @Test
    void shouldReturnUserItems() {
        firstItem.setOwner(firstUser);
        secondItem.setOwner(firstUser);

        itemRepository.save(firstItem);
        itemRepository.save(secondItem);

        long userId = firstUser.getId();

        assertTrue(itemRepository.findByOwner_Id(userId).contains(firstItem));
        assertTrue(itemRepository.findByOwner_Id(userId).contains(secondItem));
        assertTrue(itemRepository.findByOwner_Id(userId).size() == 2);
    }

    @Test
    void shouldFindItemByName() {
        firstItem.setOwner(firstUser);
        secondItem.setOwner(secondUser);
        itemRepository.save(firstItem);
        itemRepository.save(secondItem);
        /*
        первое слово описания
         */
        String targetDescription = secondItem.getDescription().split(" ")[0];

        assertEquals(List.of(secondItem), itemRepository.findByAvailableTrueAndDescriptionContainingIgnoreCase(targetDescription));
    }

    @Test
    void shouldFindItemByDescription() {
        firstItem.setOwner(firstUser);
        secondItem.setOwner(secondUser);
        itemRepository.save(firstItem);
        itemRepository.save(secondItem);

        String targetDescription = secondItem.getDescription();
        assertEquals(List.of(secondItem), itemRepository.findByAvailableTrueAndDescriptionContainingIgnoreCase(targetDescription));
    }

    private User initFirstUser() {
        User user = new User();

        user.setName("First user");
        user.setEmail("first@first.ru");
        return user;
    }

    private User initSecondUser() {
        User user = new User();

        user.setName("Second user");
        user.setEmail("second@second.ru");
        return user;
    }

    private Item initFirstItemWithoutOwner() {
        Item item = new Item();

        item.setName("First item");
        item.setDescription("First item description");
        item.setAvailable(true);
        return item;
    }

    private Item initSecondItemWithoutOwner() {
        Item item = new Item();

        item.setName("Second item");
        item.setDescription("Second item description");
        item.setAvailable(true);
        return item;
    }
}
