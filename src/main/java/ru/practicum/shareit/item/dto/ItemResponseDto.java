package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.model.Booking;

@Getter
@Setter
@AllArgsConstructor
public class ItemResponseDto {

    private long id;

    private String name;

    private String description;

    private Boolean available;

    private Booking lastBooking;

    private Booking nextBooking;
}
