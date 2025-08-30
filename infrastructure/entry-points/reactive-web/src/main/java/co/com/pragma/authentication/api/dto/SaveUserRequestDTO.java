package co.com.pragma.authentication.api.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SaveUserRequestDTO(@NotNull String name,
                                 @NotNull String surname,
                                 LocalDate dateOfBirth,
                                 @NotNull String email,
                                 String docNumber,
                                 String cellphone,
                                 @NotNull BigDecimal salaryBase) {
}
