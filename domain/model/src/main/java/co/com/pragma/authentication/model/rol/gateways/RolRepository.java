package co.com.pragma.authentication.model.rol.gateways;

import co.com.pragma.authentication.model.rol.Rol;
import reactor.core.publisher.Mono;

public interface RolRepository {

    Mono<Rol> findById(Long id);

    Mono<Rol> findByName(String name);

}
