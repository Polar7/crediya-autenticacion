package co.com.pragma.authentication.model.user;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {

    private Long id;

    private String name;

    private String surname;

    private LocalDate dateOfBirth;

    private String email;

    private String docNumber;

    private String cellphone;

    private BigDecimal salaryBase;

    private Long rolId;

    private String password;

}
