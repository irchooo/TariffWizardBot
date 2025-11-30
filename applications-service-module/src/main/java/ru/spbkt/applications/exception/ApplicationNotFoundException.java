package ru.spbkt.applications.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ApplicationNotFoundException extends RuntimeException {
    public ApplicationNotFoundException(Long id) {
        super("Заявка с ID " + id + " не найдена.");
    }
}
