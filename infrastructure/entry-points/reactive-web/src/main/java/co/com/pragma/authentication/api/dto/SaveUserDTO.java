package co.com.pragma.authentication.api.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SaveUserDTO(@NotNull String name,
                          @NotNull String surname,
                          @NotNull LocalDate dateOfBirth,
                          @NotNull String email,
                          @NotNull String docNumber,
                          @NotNull String cellphone,
                          @NotNull BigDecimal salaryBase) {
}
