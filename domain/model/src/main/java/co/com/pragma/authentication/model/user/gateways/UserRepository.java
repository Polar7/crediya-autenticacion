package co.com.pragma.authentication.model.user.gateways;

import co.com.pragma.authentication.model.user.User;
import reactor.core.publisher.Mono;

public interface UserRepository {

    Mono<User> save(User newUser);

    Mono<User> findByEmail(String email);

    Mono<User> findByDocNumber(String docNumber);

}
