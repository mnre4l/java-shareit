package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
@DisplayName("ItemRequest repository test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestRepositoryTest {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    User requester;
    ItemRequest itemRequest;

    @BeforeEach
    void init() {
        requester = new User();

        requester.setName("requester");
        requester.setEmail("requester@requester.com");

        requester = userRepository.save(requester);

        itemRequest = new ItemRequest();

        itemRequest.setUser(requester);
        itemRequest.setDescription("description");
        itemRequest.setCreated(LocalDateTime.now());
    }

    @AfterEach
    void afterEach() {
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldReturnRequestByUserId() {
        itemRequest = itemRequestRepository.save(itemRequest);
        List<ItemRequest> requests = itemRequestRepository.getItemRequestByUser_IdOrderByCreated(requester.getId());

        assertThat(requests.size(), equalTo(1));

        ItemRequest request = requests.get(0);

        assertThat(request.getId(), equalTo(itemRequest.getId()));

    }

    @Test
    void shouldReturnNothingWhenRequestByUserIdWithBadUserId() {
        itemRequest = itemRequestRepository.save(itemRequest);
        Long badId = requester.getId() + 123;
        List<ItemRequest> requests = itemRequestRepository.getItemRequestByUser_IdOrderByCreated(badId);

        assertThat(requests.size(), equalTo(0));
    }

    @Test
    void shouldReturnNothingWhenRequestByNotUserIdWithBadUserId() {
        itemRequest = itemRequestRepository.save(itemRequest);
        Long requestCreaterId = requester.getId();
        List<ItemRequest> requests = itemRequestRepository.getAllByUser_IdNot(requestCreaterId, Pageable.unpaged());

        assertThat(requests.size(), equalTo(0));
    }
}
