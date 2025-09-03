package co.com.pragma.authentication.api.dto;

import jakarta.validation.constraints.NotEmpty;

public record LoginRequestDTO(@NotEmpty String email,
                              @NotEmpty String password) {
}
