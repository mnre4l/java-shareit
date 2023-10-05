package ru.practicum.shareit.request.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.model.ItemRequestDtoOnCreate;

import java.util.Map;

@Service
public class ItemRequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests";

    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createRequest(Long userId, ItemRequestDtoOnCreate request) {
        return post("", userId, request);
    }

    public ResponseEntity<Object> getRequestsByOwner(Long ownerId) {
        return get("", ownerId);
    }

    public ResponseEntity<Object> getRequestById(Long requestId, Long userRequestFromId) {
        return get("/" + requestId, userRequestFromId);
    }

    public ResponseEntity<Object> getAllRequests(Integer from, Integer size, Long userRequestFromId) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("/all", userRequestFromId, parameters);
    }
}
