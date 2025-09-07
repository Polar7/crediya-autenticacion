package co.com.pragma.authentication.model.user;

import java.math.BigDecimal;

public record UserInfo(String email,
                       String fullName,
                       BigDecimal salaryBase) {
}
