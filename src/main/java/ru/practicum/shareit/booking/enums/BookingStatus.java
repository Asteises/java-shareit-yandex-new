package ru.practicum.shareit.booking.enums;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum BookingStatus {

    WAITING("WAITING"),
    APPROVED("CURRENT"),
    REJECTED("REJECTED"),
    CANCELED("PAST");

    private final String state;

    private static final Map<String, BookingStatus> ENUMS;

    BookingStatus(String state) {
        this.state = state;
    }

    public String getText() {
        return this.state;
    }

    static {
        Map<String, BookingStatus> map = new ConcurrentHashMap<>();
        for (BookingStatus bs: BookingStatus.values()) {
            map.put(bs.getText().toLowerCase(), bs);
        }
        ENUMS = Collections.unmodifiableMap(map);
    }

    public static BookingStatus fromString(String state) {

        return ENUMS.get(state.toLowerCase());
    }
}
