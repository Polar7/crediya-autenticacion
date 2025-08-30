package co.com.pragma.authentication.api;

import co.com.pragma.authentication.api.dto.SaveUserRequestDTO;
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

    private SaveUserRequestDTO saveUserRequestDTO;
    private User userModel;

    @BeforeEach
    void setUp() {
        saveUserRequestDTO = new SaveUserRequestDTO(
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
    void listenPOSTSaveUser_shouldReturnOkResponse_whenAllSucceed() {
        when(serverRequest.bodyToMono(SaveUserRequestDTO.class)).thenReturn(Mono.just(saveUserRequestDTO));

        when(validationUtil.validate(any(SaveUserRequestDTO.class))).thenReturn(Mono.just(saveUserRequestDTO));

        when(userMapper.toModel(any(SaveUserRequestDTO.class))).thenReturn(userModel);

        when(userUseCase.saveUser(any(User.class))).thenReturn(Mono.empty());

        Mono<ServerResponse> responseMono = handler.listenPOSTSaveUser(serverRequest);

        StepVerifier.create(responseMono)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().equals(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void listenPOSTUseCase_shouldPropagateError_whenValidationFails() {
        when(serverRequest.bodyToMono(SaveUserRequestDTO.class)).thenReturn(Mono.just(saveUserRequestDTO));

        when(validationUtil.validate(any(SaveUserRequestDTO.class)))
                .thenReturn(Mono.error(new ValidationException("Validation failed")));

        Mono<ServerResponse> responseMono = handler.listenPOSTSaveUser(serverRequest);

        StepVerifier.create(responseMono)
                .expectErrorMatches(throwable ->
                        throwable instanceof ValidationException &&
                                throwable.getMessage().equals("Validation failed")
                )
                .verify();
    }

}