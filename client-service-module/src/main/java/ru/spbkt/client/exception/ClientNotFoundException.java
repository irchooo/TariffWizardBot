package ru.spbkt.client.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND) // 404
public class ClientNotFoundException extends RuntimeException {
    public ClientNotFoundException(String message) {
        super(message);
    }

    public ClientNotFoundException(Long telegramId) {
        super("Клиент с Telegram ID " + telegramId + " не найден.");
    }
}
