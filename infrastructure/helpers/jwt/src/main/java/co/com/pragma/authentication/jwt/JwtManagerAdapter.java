package co.com.pragma.authentication.jwt;

import co.com.pragma.authentication.model.auth.gateways.JwtManager;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Date;

@Component
public class JwtManagerAdapter implements JwtManager {

    private final String secretKey;

    private final long validityInMilliseconds;

    public JwtManagerAdapter(@Value("${jwt.token.secret-key}") String secretKey, @Value("${jwt.token.expire-length}") long validityInMilliseconds) {
        this.secretKey = secretKey;
        this.validityInMilliseconds = validityInMilliseconds;
    }

    @Override
    public Mono<String> generateToken(Long idUser, String role) {
        return Mono.fromCallable(() -> {
                    Date now = new Date();
                    Date validity = new Date(now.getTime() + validityInMilliseconds);
                    Algorithm algorithm = Algorithm.HMAC256(secretKey);

                    return JWT.create()
                            .withSubject(String.valueOf(idUser))
                            .withClaim("rol", role)
                            .withIssuedAt(now)
                            .withExpiresAt(validity)
                            .sign(algorithm);
                });
    }

}
