package ru.practicum.shareit.request.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Setter
@Getter
@Entity
@Table(name = "requests", schema = "shareit")
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    User requester;

    LocalDateTime created = LocalDateTime.now();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemRequest request = (ItemRequest) o;
        return Objects.equals(id, request.id) && Objects.equals(description, request.description) && Objects.equals(created, request.created);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, created);
    }
}
