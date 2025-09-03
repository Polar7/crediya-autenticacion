package co.com.pragma.authentication.model.auth.gateways;

import reactor.core.publisher.Mono;

public interface JwtManager {

    Mono<String> generateToken(Long idUser, String role);

}
