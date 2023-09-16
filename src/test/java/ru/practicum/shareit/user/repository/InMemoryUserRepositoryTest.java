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
        inMemoryUserRepository.save(firstUser);
        inMemoryUserRepository.save(secondUser);
        /*
        проверка, что установился id
         */
        assertEquals(1, firstUser.getId());
        assertEquals(2, secondUser.getId());
    }

    @Test
    void shouldReturnUsers() {
        inMemoryUserRepository.save(firstUser);
        inMemoryUserRepository.save(secondUser);
        assertEquals(Optional.of(firstUser), inMemoryUserRepository.findById(1L));
        assertEquals(Optional.of(secondUser), inMemoryUserRepository.findById(2L));
    }

    @Test
    void shouldDeleteUser() {
        inMemoryUserRepository.save(firstUser);

        long userId = firstUser.getId();

        inMemoryUserRepository.delete(firstUser);
        assertTrue(inMemoryUserRepository.findById(userId).isEmpty());
    }

    @Test
    void shouldReturnAllUsers() {
        inMemoryUserRepository.save(firstUser);
        inMemoryUserRepository.save(secondUser);
        assertEquals(List.of(firstUser, secondUser), inMemoryUserRepository.findAll());
    }

    @Test
    void shouldDeleteAllUsers() {
        inMemoryUserRepository.save(firstUser);
        inMemoryUserRepository.save(secondUser);
        inMemoryUserRepository.deleteAll();
        assertEquals(Collections.emptyList(), inMemoryUserRepository.findAll());
    }

    @Test
    void shouldReturnEmptyOptionalWhenGetUnknownUser() {
        assertTrue(inMemoryUserRepository.findById(-1L).isEmpty());
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
