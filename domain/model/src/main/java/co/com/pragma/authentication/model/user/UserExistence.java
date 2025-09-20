package co.com.pragma.authentication.model.user;

import java.math.BigDecimal;

public record UserExistence(boolean found,
                            Long id,
                            String email,
                            BigDecimal salary) {
}
