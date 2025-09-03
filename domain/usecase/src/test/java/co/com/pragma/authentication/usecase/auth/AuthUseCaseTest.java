package co.com.pragma.authentication.usecase.auth;

import co.com.pragma.authentication.model.auth.gateways.JwtManager;
import co.com.pragma.authentication.model.rol.Rol;
import co.com.pragma.authentication.model.rol.gateways.RolRepository;
import co.com.pragma.authentication.model.user.User;
import co.com.pragma.authentication.model.user.gateways.UserRepository;
import co.com.pragma.authentication.usecase.exception.NotFoundException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthUseCaseTest {

    @InjectMocks
    AuthUseCase authUseCase;

    @Mock
    UserRepository userRepository;

    @Mock
    RolRepository rolRepository;

    @Mock
    JwtManager jwtManager;

    @Nested
    class Login {

        @Test
        void shouldReturnJwtSuccessfully() {
            when(userRepository.findByEmail(anyString())).thenReturn(Mono.just(User.builder().id(1L).email("email@pragma.com").password("password").rolId(1L).build()));
            when(rolRepository.findById(anyLong())).thenReturn(Mono.just(new Rol(1L, "CLIENT", "CLIENTE")));
            when(jwtManager.generateToken(anyLong(), anyString())).thenReturn(Mono.just("token-created"));

            StepVerifier.create(authUseCase.login("email@pragma.com", "password"))
                    .expectNext("token-created")
                    .verifyComplete();
        }

        @Test
        void shouldThrowExceptionWhenUserNotFound() {
            when(userRepository.findByEmail(anyString())).thenReturn(Mono.empty());

            StepVerifier.create(authUseCase.login("email@pragma.com", "password"))
                    .expectErrorMatches(throwable ->
                            throwable instanceof NotFoundException &&
                                    throwable.getMessage().equals(AuthUseCase.ERROR_CREDENTIALS))
                    .verify();

            verify(rolRepository, never()).findById(anyLong());
            verify(jwtManager, never()).generateToken(anyLong(), anyString());
        }

        @Test
        void shouldThrowExceptionWhenPasswordIsInvalid() {
            when(userRepository.findByEmail(anyString())).thenReturn(Mono.just(User.builder().id(1L).email("email@pragma.com").password("passwordDifferent").rolId(1L).build()));

            StepVerifier.create(authUseCase.login("email@pragma.com", "password"))
                    .expectErrorMatches(throwable ->
                            throwable instanceof NotFoundException &&
                                    throwable.getMessage().equals(AuthUseCase.ERROR_CREDENTIALS))
                    .verify();

            verify(rolRepository, never()).findById(anyLong());
            verify(jwtManager, never()).generateToken(anyLong(), anyString());
        }

    }

}