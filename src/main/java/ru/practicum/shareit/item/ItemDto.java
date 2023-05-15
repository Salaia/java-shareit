package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.request.ItemRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {

    public interface Create {
    }

    public interface Update {
    }

    Long id;
    @NotBlank(groups = {Create.class})
    String name;
    @NotBlank(groups = {Create.class})
    String description;

    @NotNull(groups = {Create.class})
    Boolean available;
    Long ownerId; // X-Sharer-User-Id показывает, от какого пользователя пришел запрос
    ItemRequest request;

}
