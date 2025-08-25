package co.com.pragma.authentication.model.user;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
//import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
//@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {

    private String name;

    private String surname;

    private String email;

    private String docNumber;

    private String cellphone;

    private BigDecimal salaryBase;

}
