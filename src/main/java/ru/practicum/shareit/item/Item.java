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
public class Item {

    Long id;
    @NotBlank
    String name;
    @NotBlank
    String description;
    @NotNull
    Boolean available;
    Long ownerId; // Пользователь, который добавляет в приложение новую вещь, будет считаться ее владельцем.
    // если вещь была создана по запросу другого пользователя, то в этом поле будет храниться ссылка на соответствующий запрос.
    ItemRequest request; // необязательное поле - только для вещей по запросу

    /*
    Customer Reviews
    после аренды можно оставить ревью. Это отдельный класс, но где он должен лежать?
    Это, видимо, будет в последующих ТЗ
     */
}
