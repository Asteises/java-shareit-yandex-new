package ru.practicum.shareit.user.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor // Пустой конструктор - обязательное условие для Entity
public class User {

    private Long id;
    private String name;
    @NotNull
    @NotBlank
    private String email;
}
