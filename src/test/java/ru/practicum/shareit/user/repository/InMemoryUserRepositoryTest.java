package ru.practicum.shareit.user.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class InMemoryUserRepositoryTest {
    private UserRepository inMemoryUserRepository;
    private User firstUser;
    private User secondUser;

    @BeforeEach
    void init() {
        firstUser = initFirstUser();
        secondUser = initSecondUser();
        inMemoryUserRepository = new InMemoryUserRepository();
    }


    @Test
    void shouldCreateUsers() {
        inMemoryUserRepository.create(firstUser);
        inMemoryUserRepository.create(secondUser);
        /*
        проверка, что установился id
         */
        assertEquals(1, firstUser.getId());
        assertEquals(2, secondUser.getId());
    }

    @Test
    void shouldReturnUsers() {
        inMemoryUserRepository.create(firstUser);
        inMemoryUserRepository.create(secondUser);
        assertEquals(Optional.of(firstUser), inMemoryUserRepository.get(1L));
        assertEquals(Optional.of(secondUser), inMemoryUserRepository.get(2L));
    }

    @Test
    void shouldDeleteUser() {
        inMemoryUserRepository.create(firstUser);

        long userId = firstUser.getId();

        inMemoryUserRepository.delete(firstUser);
        assertTrue(inMemoryUserRepository.get(userId).isEmpty());
    }

    @Test
    void shouldReturnAllUsers() {
        inMemoryUserRepository.create(firstUser);
        inMemoryUserRepository.create(secondUser);
        assertEquals(List.of(firstUser, secondUser), inMemoryUserRepository.getAll());
    }

    @Test
    void shouldDeleteAllUsers() {
        inMemoryUserRepository.create(firstUser);
        inMemoryUserRepository.create(secondUser);
        inMemoryUserRepository.deleteAll();
        assertEquals(Collections.emptyList(), inMemoryUserRepository.getAll());
    }

    @Test
    void shouldReturnEmptyOptionalWhenGetUnknownUser() {
        assertTrue(inMemoryUserRepository.get(-1L).isEmpty());
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
}
