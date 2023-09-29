package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.model.ItemInfoDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Item controller")
@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
public class ItemControllerTest {
    private static final String xSharerUserId = "X-Sharer-User-Id";
    @MockBean
    ItemService itemService;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    private ItemDto itemDto;
    private ItemInfoDto itemInfoDto;

    @BeforeEach
    void init() {
        itemDto = new ItemDto();

        itemDto.setId(1L);
        itemDto.setDescription("description");
        itemDto.setName("name");
        itemDto.setAvailable(true);
        itemDto.setRequestId(1L);

        itemInfoDto = new ItemInfoDto();
        itemInfoDto.setId(1L);
        itemInfoDto.setName("name info");

        ItemInfoDto.BookingDto bookingDto = new ItemInfoDto.BookingDto();
        bookingDto.setId(1L);
        bookingDto.setBookerId(1L);
        itemInfoDto.setLastBooking(bookingDto);
    }

    @Test
    void shouldCreateItem() throws Exception {
        when(itemService.createItem(Mockito.any(), Mockito.anyLong()))
                .thenReturn(itemDto);

        mockMvc.perform(
                        post("/items")
                                .header(xSharerUserId, 1L)
                                .content(mapper.writeValueAsString(itemDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(content().json(mapper.writeValueAsString(itemDto)))
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())));
    }

    @Test
    void shouldUpdateItem() throws Exception {
        when(itemService.updateItem(Mockito.anyLong(), Mockito.anyLong(), Mockito.any()))
                .thenReturn(itemInfoDto);

        mockMvc.perform(
                        patch("/items/{itemId}", 1L)
                                .header(xSharerUserId, 1L)
                                .content(mapper.writeValueAsString(itemDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(content().json(mapper.writeValueAsString(itemInfoDto)))
                .andExpect(jsonPath("$.id", is(itemInfoDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemInfoDto.getName())))
                .andExpect(jsonPath("$.description", is(itemInfoDto.getDescription())))
                .andExpect(jsonPath("$.lastBooking.id", is(itemInfoDto.getLastBooking().getId()), Long.class))
                .andExpect(jsonPath("$.lastBooking.bookerId", is(itemInfoDto.getLastBooking().getBookerId()), Long.class));
    }

    @Test
    void shouldReturnItem() throws Exception {
        when(itemService.getItemDtoById(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(itemInfoDto);

        mockMvc.perform(
                        get("/items/{itemId}", 1L)
                                .header(xSharerUserId, 1L)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemInfoDto)))
                .andExpect(jsonPath("$.id", is(itemInfoDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemInfoDto.getName())))
                .andExpect(jsonPath("$.description", is(itemInfoDto.getDescription())))
                .andExpect(jsonPath("$.lastBooking.id", is(itemInfoDto.getLastBooking().getId()), Long.class))
                .andExpect(jsonPath("$.lastBooking.bookerId", is(itemInfoDto.getLastBooking().getBookerId()), Long.class));
    }

    @Test
    void shouldFindItemsByText() throws Exception {
        when(itemService.findItemsBy(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(
                        get("/items/search", 1L)
                                .header(xSharerUserId, 1L)
                                .param("text", "sometext")
                                .param("from", "0")
                                .param("size", "20")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemDto))))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class));
    }
}
