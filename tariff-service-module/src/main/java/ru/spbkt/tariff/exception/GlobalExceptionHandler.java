package ru.spbkt.tariff.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.spbkt.tariff.dto.response.ErrorResponse;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    //Обработка исключений "Не найден" (404)
    @ExceptionHandler(TariffNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(TariffNotFoundException ex) {

        log.warn("Tariff not found: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getMessage()));

    }

    //Обработка бизнес-исключений и ошибок данных (400)
    @ExceptionHandler({ServiceParameterNotFoundException.class, InvalidCalculationDataException.class})
    public ResponseEntity<ErrorResponse> handleBadRequest(RuntimeException ex) {

        log.warn("Bad Request (400): {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ex.getMessage()));

    }

    //Обработка ошибок валидации (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationErrors(MethodArgumentNotValidException ex) {

        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        log.warn("Validation failed for DTO: {}", errorMessage);

        return new ErrorResponse("Ошибка валидации: " + errorMessage);

    }

    //Обработка всех остальных непредвиденных ошибок (500)
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(Exception ex) {

        log.error("Unhandled internal server error (500): {}", ex.getMessage(), ex);

        return new ErrorResponse("Внутренняя ошибка сервера. Обратитесь в поддержку.");

    }

}
