package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.CommentDtoInput;
import ru.practicum.shareit.item.dto.CommentDtoOutput;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class CommentMapper {

    public Comment toComment(CommentDtoInput input, User author, Item item) {
        Comment comment = new Comment();
        comment.setText(input.getText());
        comment.setAuthor(author);
        comment.setItem(item);
        return comment;
    }

    public CommentDtoOutput toCommentDto(Comment comment) {
        CommentDtoOutput output = new CommentDtoOutput();
        output.setId(comment.getId());
        output.setText(comment.getText());
        output.setCreated(comment.getCreated());
        output.setAuthorName(comment.getAuthor().getName());
        return output;
    }

    public List<CommentDtoOutput> toCommentDtoList(List<Comment> comments) {
        List<CommentDtoOutput> result = new ArrayList<>();
        for (Comment comment : comments) {
            result.add(toCommentDto(comment));
        }
        return result;
    }
}
