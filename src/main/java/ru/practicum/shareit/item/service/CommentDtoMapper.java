package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentDto;

@Component
@RequiredArgsConstructor
public class CommentDtoMapper {
    private final ModelMapper mapper;

    public Comment fromDto(CommentDto commentDto) {
        return mapper.map(commentDto, Comment.class);
    }

    public CommentDto toDto(Comment comment) {
        return mapper.map(comment, CommentDto.class);
    }
}
