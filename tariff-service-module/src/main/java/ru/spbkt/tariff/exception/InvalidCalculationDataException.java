package ru.spbkt.tariff.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST) // 400 Bad Request
public class InvalidCalculationDataException extends RuntimeException {

    public InvalidCalculationDataException(String message) {

        super(message);

    }

}
