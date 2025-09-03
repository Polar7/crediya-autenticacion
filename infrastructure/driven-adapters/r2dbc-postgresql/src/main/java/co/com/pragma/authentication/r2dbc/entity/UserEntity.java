package co.com.pragma.authentication.r2dbc.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table("usuarios")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column("id_usuario")
    private Long id;

    @Column("nombre")
    private String name;

    @Column("apellido")
    private String surname;

    @Column("fecha_nacimiento")
    private LocalDate dateOfBirth;

    @Column("correo_electronico")
    private String email;

    @Column("documento_identidad")
    private String docNumber;

    @Column("celular")
    private String cellphone;

    @Column("salario_base")
    private BigDecimal salaryBase;

    @Column("id_rol")
    private Long rolId;

    @Column("contrasenia")
    private String password;

}
