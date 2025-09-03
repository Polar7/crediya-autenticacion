package co.com.pragma.authentication.model.rol;

import lombok.Getter;

@Getter
public enum RolEnum {

    CLIENT("CLIENTE"),
    BANKER("ASESOR"),
    ADMIN("ADMINISTRADOR");

    private final String name;

    RolEnum(String name) {
        this.name = name;
    }

}
