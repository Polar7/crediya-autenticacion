package co.com.pragma.authentication.api;

import co.com.pragma.authentication.api.dto.SaveUserDTO;
import co.com.pragma.authentication.api.mapper.UserMapper;
import co.com.pragma.authentication.model.user.User;
import co.com.pragma.authentication.usecase.user.UserUseCase;
import co.com.pragma.authentication.validation.ValidationException;
import co.com.pragma.authentication.validation.ValidationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HandlerTest {

    @Mock
    private UserUseCase userUseCase;

    @Mock
    private UserMapper userMapper;

    @Mock
    private ValidationUtil validationUtil;

    @Mock
    private ServerRequest serverRequest;

    @InjectMocks
    private Handler handler;

    private SaveUserDTO saveUserDTO;
    private User userModel;

    @BeforeEach
    void setUp() {
        saveUserDTO = new SaveUserDTO(
                "John", "Doe", LocalDate.of(1990, 1, 1),
                "john.doe@example.com", "12345", "123456789",
                new BigDecimal("1500000")
        );

        userModel = User.builder()
                .name("John")
                .email("john.doe@example.com")
                .build();
    }

    @Test
    void listenPOSTUseCase_shouldReturnOkResponse_whenAllSucceed() {
        // Mock the request body
        when(serverRequest.bodyToMono(SaveUserDTO.class)).thenReturn(Mono.just(saveUserDTO));

        // Mock the successful validation flow
        when(validationUtil.validate(any(SaveUserDTO.class))).thenReturn(Mono.just(saveUserDTO));

        // Mock the successful mapping
        when(userMapper.toModel(any(SaveUserDTO.class))).thenReturn(userModel);

        // Mock the successful use case
        when(userUseCase.saveUser(any(User.class))).thenReturn(Mono.empty());

        // Use StepVerifier to test the Mono<ServerResponse>
        Mono<ServerResponse> responseMono = handler.listenPOSTUseCase(serverRequest);

        StepVerifier.create(responseMono)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().equals(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void listenPOSTUseCase_shouldPropagateError_whenValidationFails() {
        // Mock the request body
        when(serverRequest.bodyToMono(SaveUserDTO.class)).thenReturn(Mono.just(saveUserDTO));

        // Mock the validation failure
        when(validationUtil.validate(any(SaveUserDTO.class)))
                .thenReturn(Mono.error(new ValidationException("Validation failed")));

        // The Mono from the handler should emit an error
        Mono<ServerResponse> responseMono = handler.listenPOSTUseCase(serverRequest);

        StepVerifier.create(responseMono)
                .expectErrorMatches(throwable ->
                        throwable instanceof ValidationException &&
                                throwable.getMessage().equals("Validation failed")
                )
                .verify();
    }

}