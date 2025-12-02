package ru.spbkt.client.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientProfileUpdateRequest {

    @Size(min = 1, max = 50, message = "Имя должно быть от 1 до 50 символов")
    private String firstName;

    @Size(min = 1, max = 50, message = "Фамилия должна быть от 1 до 50 символов")
    private String lastName;

    @Email(message = "Некорректный email")
    @Size(max = 100)
    private String email;
}
