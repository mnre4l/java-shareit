package ru.practicum.shareit.user.model;


import java.util.Optional;

public enum UserBookingStates {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static Optional<UserBookingStates> from(String someString) {
        for (UserBookingStates state : UserBookingStates.values()) {
            if (state.name().equals(someString)) return Optional.ofNullable(state);
        }
        return Optional.empty();
    }
}

