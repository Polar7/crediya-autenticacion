package co.com.pragma.authentication.r2dbc.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("roles")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RolEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column("id_rol")
    private Long id;

    @Column("nombre")
    private String name;

    @Column("descripcion")
    private String description;

}
