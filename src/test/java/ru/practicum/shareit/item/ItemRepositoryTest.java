package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DisplayName("Item repository test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DataJpaTest
public class ItemRepositoryTest {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    Item item;
    User owner;

    @BeforeEach
    void init() {
        owner = new User();

        owner.setId(1L);
        owner.setName("test owner");
        owner.setEmail("test@test.com");

        owner = userRepository.save(owner);
        item = new Item();

        item.setName("test item");
        item.setOwner(owner);
        item.setAvailable(true);
        item.setDescription("test description");

        item = itemRepository.save(item);
    }

    @AfterEach
    void afterEach() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldFindItemByOwnerId() {
        List<Item> findResult = itemRepository.findByOwner_IdOrderByIdAsc(owner.getId());

        assertThat(findResult.size(), equalTo(1));

        Item resultItem = findResult.get(0);

        assertThat(resultItem.getName(), equalTo(item.getName()));
        assertThat(resultItem.getDescription(), equalTo(item.getDescription()));
        assertThat(resultItem.getOwner().getId(), equalTo(item.getOwner().getId()));
        assertThat(resultItem.getAvailable(), equalTo(item.getAvailable()));
    }

    @Test
    void shouldFindTestItemByDescription() {
        String text = "est";
        List<Item> findResult = itemRepository
                .findByAvailableTrueAndDescriptionContainingIgnoreCase(text, Pageable.unpaged());

        assertThat(findResult.size(), equalTo(1));

        Item resultItem = findResult.get(0);

        assertThat(resultItem.getName(), equalTo(item.getName()));
        assertThat(resultItem.getDescription(), equalTo(item.getDescription()));
        assertThat(resultItem.getOwner().getId(), equalTo(item.getOwner().getId()));
        assertThat(resultItem.getAvailable(), equalTo(item.getAvailable()));
    }

    @Test
    void shouldFindNothingByDescription() {
        String text = "bad description";
        List<Item> findResult = itemRepository
                .findByAvailableTrueAndDescriptionContainingIgnoreCase(text, Pageable.unpaged());

        assertThat(findResult.size(), equalTo(0));
    }

    @Test
    void shouldFindNothingWhenUnavailable() {
        item.setAvailable(false);
        item = itemRepository.save(item);

        String text = item.getDescription();
        List<Item> findResult = itemRepository
                .findByAvailableTrueAndDescriptionContainingIgnoreCase(text, Pageable.unpaged());

        assertThat(findResult.size(), equalTo(0));

    }
}
