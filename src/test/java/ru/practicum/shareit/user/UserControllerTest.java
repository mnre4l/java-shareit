package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("User controller")
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {
    private static final String xSharerUserId = "X-Sharer-User-Id";
    private static final long userId = 1L;
    @MockBean
    UserService userService;

    @Autowired
    MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    UserDto userDto;


    @BeforeEach
    void setUp() {
        userDto = new UserDto();

        userDto.setId(userId);
        userDto.setName("name");
        userDto.setEmail("user@user.com");
    }

    @Test
    void shouldReturnAllUsers() throws Exception {
        List<UserDto> users = List.of(userDto);

        when(userService.getAll())
                .thenReturn(users);

        mockMvc.perform(
                        get("/users")
                                .header(xSharerUserId, userId)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(users)))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userDto.getName())))
                .andExpect(jsonPath("$[0].email", is(userDto.getEmail())));
    }

    @Test
    void shouldReturnUser() throws Exception {
        when(userService.getUserDtoById(Mockito.anyLong()))
                .thenReturn(userDto);

        mockMvc.perform(
                        get("/users/" + userId)
                                .header(xSharerUserId, userId)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(userDto)))
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void shouldAddUser() throws Exception {
        when(userService.createUser(Mockito.any()))
                .thenReturn(userDto);

        mockMvc.perform(
                        post("/users")
                                .content(mapper.writeValueAsString(userDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(content().json(mapper.writeValueAsString(userDto)))
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void shouldUpdateUser() throws Exception {
        when(userService.updateUser(Mockito.anyLong(), Mockito.any()))
                .thenReturn(userDto);

        mockMvc.perform(
                        patch("/users/{userId}", 1L)
                                .content(mapper.writeValueAsString(userDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(content().json(mapper.writeValueAsString(userDto)))
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void shouldDeleteUser() throws Exception {
        mockMvc.perform(
                        delete("/users/{userId}", 1L)
                )
                .andExpect(status().isOk());
    }
}
