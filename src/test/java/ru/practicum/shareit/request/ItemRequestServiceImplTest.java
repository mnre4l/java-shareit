package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.request.model.ItemRequestDtoAfterCreate;
import ru.practicum.shareit.request.model.ItemRequestDtoInfo;
import ru.practicum.shareit.request.model.ItemRequestDtoOnCreate;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DisplayName("Request service")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceImplTest {
    private final ItemRequestService itemRequestService;
    private final UserRepository userRepository;
    User requester;

    @BeforeEach
    void init() {
        requester = new User();

        requester.setName("requester");
        requester.setEmail("requester@mail.ru");

        requester = userRepository.save(requester);
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
    }

    @Test
    void shouldCreateRequest() {
        ItemRequestDtoOnCreate itemRequestDtoOnCreate = new ItemRequestDtoOnCreate();
        itemRequestDtoOnCreate.setDescription("description");

        ItemRequestDtoAfterCreate request = itemRequestService.createRequest(requester.getId(), itemRequestDtoOnCreate);

        assertThat(request.getDescription(), equalTo(itemRequestDtoOnCreate.getDescription()));
    }

    @Test
    void shouldNotCreateRequestWhenUnknownUser() {
        ItemRequestDtoOnCreate itemRequestDtoOnCreate = new ItemRequestDtoOnCreate();
        itemRequestDtoOnCreate.setDescription("description");

        assertThrows(NotFoundException.class, () -> {
            itemRequestService.createRequest(Long.MAX_VALUE, itemRequestDtoOnCreate);
        });
    }

    @Test
    void shouldReturnRequestsByUser() {
        ItemRequestDtoOnCreate itemRequestDtoOnCreate = new ItemRequestDtoOnCreate();
        itemRequestDtoOnCreate.setDescription("description");

        ItemRequestDtoAfterCreate request = itemRequestService.createRequest(requester.getId(), itemRequestDtoOnCreate);

        List<ItemRequestDtoInfo> requests = itemRequestService.getRequestsByOwner(requester.getId());

        assertThat(requests.size(), equalTo(1));
        assertThat(requests.get(0).getId(), equalTo(request.getId()));
        assertThat(requests.get(0).getDescription(), equalTo(request.getDescription()));
    }

    @Test
    void shouldThrowNotFoundWhenGetRequestByUnknownUser() {
        ItemRequestDtoOnCreate itemRequestDtoOnCreate = new ItemRequestDtoOnCreate();
        itemRequestDtoOnCreate.setDescription("description");

        assertThrows(NotFoundException.class, () -> {
            itemRequestService.getRequestsByOwner(Long.MAX_VALUE);
        });
    }

    @Test
    void shouldReturnRequestById() {
        ItemRequestDtoOnCreate itemRequestDtoOnCreate = new ItemRequestDtoOnCreate();
        itemRequestDtoOnCreate.setDescription("description");

        ItemRequestDtoAfterCreate request = itemRequestService.createRequest(requester.getId(), itemRequestDtoOnCreate);
        ItemRequestDtoInfo requestDtoInfo = itemRequestService.getRequestById(request.getId(), requester.getId());

        assertThat(request.getDescription(), equalTo(requestDtoInfo.getDescription()));
    }

    @Test
    void shouldThrowNotFoundWhenRequestByIdWithBadId() {
        assertThrows(NotFoundException.class, () -> {
            itemRequestService.getRequestById(Long.MAX_VALUE, 1L);
        });
    }

    @Test
    void shouldThrowNotFoundWhenRequestAllWithBadUserId() {
        assertThrows(NotFoundException.class, () -> {
            itemRequestService.getAllRequests(0, 1, Long.MAX_VALUE);
        });
    }

    @Test
    void shouldReturnAllWhenRequestAllWithBadUserId() {
        ItemRequestDtoOnCreate itemRequestDtoOnCreate = new ItemRequestDtoOnCreate();
        itemRequestDtoOnCreate.setDescription("description");

        ItemRequestDtoAfterCreate firstRequest = itemRequestService.createRequest(requester.getId(), itemRequestDtoOnCreate);

        ItemRequestDtoOnCreate itemRequestDtoOnCreateSecond = new ItemRequestDtoOnCreate();
        itemRequestDtoOnCreateSecond.setDescription("description2");

        ItemRequestDtoAfterCreate secondRequest = itemRequestService.createRequest(requester.getId(), itemRequestDtoOnCreateSecond);

        User user = new User();
        user.setName("name");
        user.setEmail("email@email.ru");
        user = userRepository.save(user);

        List<ItemRequestDtoInfo> requests = itemRequestService.getAllRequests(0, 20, user.getId());

        assertThat(requests.size(), equalTo(2));
        assertThat(requests.stream()
                .map(r -> r.getId())
                .collect(Collectors.toList()), equalTo(List.of(firstRequest.getId(), secondRequest.getId())));
    }
}
