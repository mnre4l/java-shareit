package ru.practicum.shareit.item.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.model.CommentDto;
import ru.practicum.shareit.item.model.ItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createItem(ItemDto itemDto, long ownerId) {
        return post("", ownerId, itemDto);
    }

    public ResponseEntity<Object> updateItem(Long itemId, long userIdRequestFrom, ItemDto itemDto) {
        return patch("/" + itemId, userIdRequestFrom, itemDto);
    }

    public ResponseEntity<Object> getItemDtoById(Long itemId, long userIdRequestFrom) {
        return get("/" + itemId, userIdRequestFrom);
    }

    public ResponseEntity<Object> getItemsByOwnerId(long userIdRequestFrom) {
        return get("", userIdRequestFrom);
    }

    public ResponseEntity<Object> findItemsBy(String text, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search?text={text}&from={from}&size={size}", parameters);
    }

    public ResponseEntity<Object> addComment(long userIdRequestFrom, Long itemId, CommentDto commentDto) {
        return post("/" + itemId + "/" + "comment", userIdRequestFrom, commentDto);
    }
}
