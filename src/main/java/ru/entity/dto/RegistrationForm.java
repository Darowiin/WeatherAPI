package ru.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistrationForm (
        @NotBlank(message = "Имя пользователя не может быть пустым.")
        @Size(min = 3, max = 20, message = "Имя пользователя должно быть от 3 до 20 символов.")
        String username,

        @NotBlank(message = "Пароль не может быть пустым.")
        @Size(min = 6, message = "Пароль должен быть не менее 6 символов.")
        String password,

        @NotBlank(message = "Повтор пароля не может быть пустым.")
        String repeatedPassword
) {}