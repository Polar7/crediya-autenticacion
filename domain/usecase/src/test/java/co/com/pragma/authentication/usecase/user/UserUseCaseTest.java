package co.com.pragma.authentication.usecase.user;

import co.com.pragma.authentication.model.rol.Rol;
import co.com.pragma.authentication.model.rol.gateways.RolRepository;
import co.com.pragma.authentication.model.user.User;
import co.com.pragma.authentication.model.user.UserExistence;
import co.com.pragma.authentication.model.user.gateways.UserRepository;
import co.com.pragma.authentication.usecase.exception.InvalidInputDataException;
import co.com.pragma.authentication.usecase.exception.UserAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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

    @InjectMocks
    UserUseCase userUseCase;

    @Mock
    UserRepository userRepository;

    @Mock
    RolRepository rolRepository;

    User validUser;

    @BeforeEach
    void setup() {
        validUser = User.builder()
                .name("John")
                .surname("Doe")
                .email("john.doe@bancolombia.com")
                .salaryBase(new BigDecimal("10000000"))
                .build();
    }

    @Nested
    class FindEmailUserByDocNumber {

        @Test
        void shouldReturnUser() {
            when(userRepository.findByDocNumber(validUser.getEmail())).thenReturn(Mono.just(validUser));

            StepVerifier.create(userUseCase.findEmailUserByDocNumber(validUser.getEmail()))
                    .expectNext(new UserExistence(true, validUser.getId(), validUser.getEmail()))
                    .verifyComplete();
        }

        @Test
        void shouldReturnUserNotFound() {
            when(userRepository.findByDocNumber(validUser.getEmail())).thenReturn(Mono.empty());

            StepVerifier.create(userUseCase.findEmailUserByDocNumber(validUser.getEmail()))
                    .expectNext(new UserExistence(false, null, null))
                    .verifyComplete();
        }

    }

    @Nested
    class SaveUser {

        @Test
        void shouldSaveUserSuccessfully() {
            when(userRepository.findByEmail(validUser.getEmail())).thenReturn(Mono.empty());
            when(userRepository.save(any(User.class))).thenReturn(Mono.just(validUser));
            when(rolRepository.findByName(anyString())).thenReturn(Mono.just(new Rol(1L, "TEST", "TEST")));

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
                            throwable instanceof InvalidInputDataException &&
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
                            throwable instanceof InvalidInputDataException &&
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

    @Nested
    class ValidateRequiredFieldsInSaveUser {
        @Test
        void shouldThrowExceptionWhenNameIsNull() {
            User invalidUser = validUser.toBuilder()
                    .name(null)
                    .build();

            StepVerifier.create(userUseCase.saveUser(invalidUser))
                    .expectErrorMatches(throwable ->
                            throwable instanceof InvalidInputDataException &&
                                    throwable.getMessage().equals("Name cannot be null or empty.")
                    )
                    .verify();

            invalidUser = validUser.toBuilder()
                    .name("")
                    .build();

            StepVerifier.create(userUseCase.saveUser(invalidUser))
                    .expectErrorMatches(throwable ->
                            throwable instanceof InvalidInputDataException &&
                                    throwable.getMessage().equals("Name cannot be null or empty.")
                    )
                    .verify();
        }

        @Test
        void shouldThrowExceptionWhenSurnameIsNull() {
            User invalidUser = validUser.toBuilder()
                    .surname(null)
                    .build();

            StepVerifier.create(userUseCase.saveUser(invalidUser))
                    .expectErrorMatches(throwable ->
                            throwable instanceof InvalidInputDataException &&
                                    throwable.getMessage().equals("Surname cannot be null or empty.")
                    )
                    .verify();

            invalidUser = validUser.toBuilder()
                    .surname("")
                    .build();

            StepVerifier.create(userUseCase.saveUser(invalidUser))
                    .expectErrorMatches(throwable ->
                            throwable instanceof InvalidInputDataException &&
                                    throwable.getMessage().equals("Surname cannot be null or empty.")
                    )
                    .verify();
        }

        @Test
        void shouldThrowExceptionWhenEmailIsNull() {
            User invalidUser = validUser.toBuilder()
                    .email(null)
                    .build();

            StepVerifier.create(userUseCase.saveUser(invalidUser))
                    .expectErrorMatches(throwable ->
                            throwable instanceof InvalidInputDataException &&
                                    throwable.getMessage().equals("Email cannot be null or empty.")
                    )
                    .verify();

            invalidUser = validUser.toBuilder()
                    .email("")
                    .build();

            StepVerifier.create(userUseCase.saveUser(invalidUser))
                    .expectErrorMatches(throwable ->
                            throwable instanceof InvalidInputDataException &&
                                    throwable.getMessage().equals("Email cannot be null or empty.")
                    )
                    .verify();
        }

        @Test
        void shouldThrowExceptionWhenSalaryBaseIsNull() {
            User invalidUser = validUser.toBuilder()
                    .salaryBase(null)
                    .build();

            StepVerifier.create(userUseCase.saveUser(invalidUser))
                    .expectErrorMatches(throwable ->
                            throwable instanceof InvalidInputDataException &&
                                    throwable.getMessage().equals("Salary base cannot be null.")
                    )
                    .verify();
        }

    }

}