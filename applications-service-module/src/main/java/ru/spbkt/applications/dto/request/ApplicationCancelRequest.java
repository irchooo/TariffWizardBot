package ru.spbkt.applications.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ApplicationCancelRequest {

    @NotBlank(message = "Укажите причину отмены")
    @Size(max = 255, message = "Причина слишком длинная")
    private String reason;
}
