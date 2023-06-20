package ru.practicum.shareit.exception;

public class BookingEndBeforeStartException extends RuntimeException {
    public BookingEndBeforeStartException(String message) {
        super(message);
    }
}

