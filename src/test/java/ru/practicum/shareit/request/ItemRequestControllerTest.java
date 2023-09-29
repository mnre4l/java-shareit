package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.model.ItemRequestDtoAfterCreate;
import ru.practicum.shareit.request.model.ItemRequestDtoInfo;
import ru.practicum.shareit.request.model.ItemRequestDtoOnCreate;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Request controller")
@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
public class ItemRequestControllerTest {
    private static final String xSharerUserId = "X-Sharer-User-Id";
    private LocalDateTime created = LocalDateTime.now();
    @MockBean
    ItemRequestService itemRequestService;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    private ItemRequestDtoOnCreate itemRequestDtoOnCreate;
    private ItemRequestDtoAfterCreate itemRequestDtoAfterCreate;
    private ItemRequestDtoInfo itemRequestDtoInfo;

    @BeforeEach
    void init() {
        itemRequestDtoOnCreate = new ItemRequestDtoOnCreate();

        itemRequestDtoOnCreate.setDescription("description");

        itemRequestDtoAfterCreate = new ItemRequestDtoAfterCreate();

        itemRequestDtoAfterCreate.setId(1L);
        itemRequestDtoAfterCreate.setDescription("description");
        itemRequestDtoAfterCreate.setCreated(created);

        itemRequestDtoInfo = new ItemRequestDtoInfo();

        itemRequestDtoInfo.setId(1L);
        itemRequestDtoInfo.setCreated(created);
    }

    @Test
    void shouldCreateRequest() throws Exception {
        when(itemRequestService.createRequest(Mockito.anyLong(), Mockito.any()))
                .thenReturn(itemRequestDtoAfterCreate);

        mockMvc.perform(
                        post("/requests")
                                .header(xSharerUserId, 1L)
                                .content(mapper.writeValueAsString(itemRequestDtoOnCreate))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(content().json(mapper.writeValueAsString(itemRequestDtoAfterCreate)))
                .andExpect(jsonPath("$.id", is(itemRequestDtoAfterCreate.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDtoAfterCreate.getDescription())));
    }

    @Test
    void shouldReturnRequest() throws Exception {
        when(itemRequestService.getRequestsByOwner(Mockito.anyLong()))
                .thenReturn(List.of(itemRequestDtoInfo));

        mockMvc.perform(
                        get("/requests")
                                .header(xSharerUserId, 1L)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemRequestDtoInfo))))
                .andExpect(jsonPath("$[0].id", is(itemRequestDtoInfo.getId()), Long.class))
                .andExpect(jsonPath(("$[0].description"), is(itemRequestDtoInfo.getDescription())));
    }

    @Test
    void shouldReturnRequestById() throws Exception {
        when(itemRequestService.getRequestById(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(itemRequestDtoInfo);

        mockMvc.perform(
                        get("/requests/{requestId}", 1L)
                                .header(xSharerUserId, 1L)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemRequestDtoInfo)))
                .andExpect(jsonPath("$.description", is(itemRequestDtoInfo.getDescription())))
                .andExpect(jsonPath("$.id", is(itemRequestDtoInfo.getId()), Long.class));
    }
}
