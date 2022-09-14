package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
public class UserDto {

    private long id;
    @NotNull
    @NotBlank
    private String name;
    @NotNull
    @NotBlank
    private String email;
}
