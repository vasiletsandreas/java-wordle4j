package ru.yandex.practicum;

public class WordNotFoundException extends RuntimeException {
    public WordNotFoundException(String message) {
        super(message);
    }

    public WordNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}