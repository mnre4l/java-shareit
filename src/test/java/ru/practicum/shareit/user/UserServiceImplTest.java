package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.service.UserDtoMapper;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DisplayName("User service")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplTest {
    private final EntityManager em;
    private final UserService userService;
    private final UserDtoMapper mapper;

    @Test
    void shouldCreateUserFromDto() {
        UserDto userDto = getUserDto();
        userDto = userService.createUser(userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User user = query.setParameter("id", userDto.getId())
                .getSingleResult();

        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void shouldReturnAllUsers() {
        UserDto userDto = getUserDto();
        userDto = userService.createUser(userDto);

        UserDto userDtoTwo = new UserDto();

        userDtoTwo.setEmail("email2@email.ru");
        userDtoTwo.setName("user2");

        userDtoTwo = userService.createUser(userDto);

        List<UserDto> expectedUsers = userService.getAll();

        TypedQuery<User> query = em.createQuery("Select u from User u", User.class);
        List<UserDto> users = query.getResultList().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());

        assertThat(expectedUsers, equalTo(users));
    }

    @Test
    void shouldReturnUserDto() {
        UserDto userDto = getUserDto();
        userDto = userService.createUser(userDto);

        UserDto returnedUserDto = userService.getUserDtoById(userDto.getId());

        assertThat(userDto, equalTo(returnedUserDto));
    }

    @Test
    void shouldThrowExceptionWhenGetOrUpdateUserWithUnknownId() {
        assertThrows(NotFoundException.class, () -> {
            userService.getUserDtoById(Long.MAX_VALUE);
        });
        /*
        userDto = null
         */
        assertThrows(NotFoundException.class, () -> {
            userService.updateUser(Long.MAX_VALUE, null);
        });
    }

    @Test
    void shouldUpdateUser() {
        UserDto userDto = getUserDto();
        userDto = userService.createUser(userDto);
        String oldName = userDto.getName();

        userDto.setName("updated name");
        userDto = userService.updateUser(userDto.getId(), userDto);
        String newName = userService
                .getUserDtoById(userDto.getId())
                .getName();

        assertThat(newName, equalTo("updated name"));
    }

    @Test
    void shouldDeleteUser() {
        UserDto userDto = getUserDto();
        userDto = userService.createUser(userDto);
        final Long id = userDto.getId();

        userService.deleteUser(id);

        assertThrows(NotFoundException.class, () -> {
            userService.getUserDtoById(id);
        });
    }

    private UserDto getUserDto() {
        UserDto userDto = new UserDto();

        userDto.setEmail("email@email.ru");
        userDto.setName("user");
        return userDto;
    }
}
