package co.com.pragma.authentication.api;

import co.com.pragma.authentication.api.config.UserPath;
import co.com.pragma.authentication.api.dto.SaveUserRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RouterRestTest {

    private WebTestClient webTestClient;

    @Mock
    private Handler handler;

    @Mock
    private UserPath userPath;

    @InjectMocks
    private RouterRest routerRest;

    @BeforeEach
    void setup() {
        when(userPath.getUsers()).thenReturn("/api/v1/usuarios");
        when(userPath.getUserByDocument()).thenReturn("/api/v1/usuarios/{docNumber}");
        webTestClient = WebTestClient.bindToRouterFunction(routerRest.routerFunction(handler)).build();
    }

    @Test
    void testSaveUser_shouldReturnOkResponse_whenHandlerSucceeds() {
        when(handler.listenPOSTSaveUser(any()))
                .thenReturn(Mono.just(ServerResponse.ok().bodyValue("Successful").block()));

        SaveUserRequestDTO userDto = new SaveUserRequestDTO(
                "John", "Doe", LocalDate.of(1990, 1, 1),
                "john.doe@example.com", "12345", "123456789",
                new BigDecimal("1500000")
        );

        webTestClient.post()
                .uri("/api/v1/usuarios")
                .bodyValue(userDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("Successful");
    }

    @Test
    void testSaveUser_shouldReturnBadRequest_whenHandlerReturnsError() {
        when(handler.listenPOSTSaveUser(any()))
                .thenReturn(Mono.just(ServerResponse.badRequest().bodyValue("Validation error").block()));

        SaveUserRequestDTO userDto = new SaveUserRequestDTO(
                "John", "Doe", LocalDate.of(1990, 1, 1),
                "john.doe@example.com", "12345", "123456789",
                new BigDecimal("1500000")
        );

        webTestClient.post()
                .uri("/api/v1/usuarios")
                .bodyValue(userDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .isEqualTo("Validation error");
    }

}
