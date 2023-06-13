package ru.practicum.shareit.exception;

public class BookingEndEqualStartException extends RuntimeException {
    public BookingEndEqualStartException(String message) {
        super(message);
    }
}

