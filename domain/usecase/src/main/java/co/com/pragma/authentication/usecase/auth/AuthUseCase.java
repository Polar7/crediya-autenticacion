package co.com.pragma.authentication.usecase.auth;

import co.com.pragma.authentication.model.auth.gateways.JwtManager;
import co.com.pragma.authentication.model.rol.Rol;
import co.com.pragma.authentication.model.rol.gateways.RolRepository;
import co.com.pragma.authentication.model.user.User;
import co.com.pragma.authentication.model.user.gateways.UserRepository;
import co.com.pragma.authentication.usecase.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class AuthUseCase {

    public static final String ERROR_CREDENTIALS = "Credentials invalid.";

    private final UserRepository userRepository;

    private final RolRepository rolRepository;

    private final JwtManager jwtManager;

    public Mono<String> login(String email, String password) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new NotFoundException(ERROR_CREDENTIALS)))
                .flatMap(userFound -> {
                    if(!userFound.getPassword().equalsIgnoreCase(password)) {
                        return Mono.error(new NotFoundException(ERROR_CREDENTIALS));
                    }
                    return getRolName(userFound.getRolId())
                            .zipWith(Mono.just(userFound));
                })
                .flatMap(tuple -> {
                    String rolName = tuple.getT1();
                    User userFound = tuple.getT2();

                    return jwtManager.generateToken(userFound.getId(), rolName);
                });
    }

    private Mono<String> getRolName(Long rolId) {
        return rolRepository.findById(rolId)
                .switchIfEmpty(Mono.error(new NotFoundException("Rol not found")))
                .map(Rol::getName);
    }

}
