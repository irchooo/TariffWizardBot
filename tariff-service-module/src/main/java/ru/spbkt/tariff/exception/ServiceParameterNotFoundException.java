package ru.spbkt.tariff.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST) // 400 Bad Request
public class ServiceParameterNotFoundException extends RuntimeException {

    public ServiceParameterNotFoundException(Integer id) {

        super("Параметр услуги с ID " + id + " не найден или неактивен.");

    }

}
