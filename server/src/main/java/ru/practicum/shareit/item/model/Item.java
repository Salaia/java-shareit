package ru.practicum.shareit.item.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Entity
@Table(name = "items", schema = "shareit")
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class Item {

    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;
    String description;

    @Column(name = "is_available")
    Boolean available;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    User owner;

    @OneToOne
    ItemRequest request;
}
