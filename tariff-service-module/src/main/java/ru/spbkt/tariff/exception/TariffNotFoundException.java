package ru.spbkt.tariff.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND) // 404
public class TariffNotFoundException extends RuntimeException {

    public TariffNotFoundException(Integer id) {

        super("Тариф с ID " + id + " не найден.");

    }

}
