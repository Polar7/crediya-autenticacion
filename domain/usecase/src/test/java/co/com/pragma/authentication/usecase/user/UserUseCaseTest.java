package co.com.pragma.authentication.usecase.user;

import co.com.pragma.authentication.model.user.User;
import co.com.pragma.authentication.model.user.gateways.UserRepository;
import co.com.pragma.authentication.usecase.exception.UserAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserUseCase userUseCase;

    private User validUser;

    @BeforeEach
    void setup() {
        validUser = User.builder()
                .name("John")
                .email("john.doe@bancolombia.com")
                .salaryBase(new BigDecimal("10000000"))
                .build();
    }

    @Test
    void shouldSaveUserSuccessfully() {
        when(userRepository.findByEmail(validUser.getEmail())).thenReturn(Mono.empty());
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(validUser));

        StepVerifier.create(userUseCase.saveUser(validUser))
                .expectComplete()
                .verify();
    }

    @Test
    void shouldThrowExceptionWhenSalaryIsExceeded() {
        User invalidSalaryUser = validUser.toBuilder()
                .salaryBase(new BigDecimal("16000000"))
                .build();

        StepVerifier.create(userUseCase.saveUser(invalidSalaryUser))
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().equals("The salary cannot exceed 15'000.000")
                )
                .verify();

        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    void shouldThrowExceptionWhenEmailDomainIsInvalid() {
        User invalidEmailUser = validUser.toBuilder()
                .email("john.doe@gmail.com")
                .build();

        StepVerifier.create(userUseCase.saveUser(invalidEmailUser))
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().equals("The email must be from an authorized domain.")
                )
                .verify();

        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        User existingUser = validUser.toBuilder().build();

        when(userRepository.findByEmail(existingUser.getEmail())).thenReturn(Mono.just(existingUser));

        StepVerifier.create(userUseCase.saveUser(existingUser))
                .expectError(UserAlreadyExistsException.class)
                .verify();

        verify(userRepository, never()).save(any(User.class));
    }

}