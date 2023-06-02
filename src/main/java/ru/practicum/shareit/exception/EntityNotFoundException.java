package ru.practicum.shareit.exception;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String entity, Long id) {
        super(entity + " c id " + id + " не найден/а.");
    }

}
