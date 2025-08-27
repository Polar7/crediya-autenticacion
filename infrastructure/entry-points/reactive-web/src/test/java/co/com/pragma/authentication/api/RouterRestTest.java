package co.com.pragma.authentication.api;

import co.com.pragma.authentication.api.config.UserPath;
import co.com.pragma.authentication.api.dto.SaveUserDTO;
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
        // Set up the mock for the path
        when(userPath.getUsers()).thenReturn("/api/v1/usuarios");
        // Initialize WebTestClient with your router function
        webTestClient = WebTestClient.bindToRouterFunction(routerRest.routerFunction(handler)).build();
    }

    @Test
    void testSaveUser_shouldReturnOkResponse_whenHandlerSucceeds() {
        // Mock the handler to return a successful response
        when(handler.listenPOSTUseCase(any()))
                .thenReturn(Mono.just(ServerResponse.ok().bodyValue("Successful").block()));

        SaveUserDTO userDto = new SaveUserDTO(
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
        // Mock the handler to return a 400 Bad Request response
        when(handler.listenPOSTUseCase(any()))
                .thenReturn(Mono.just(ServerResponse.badRequest().bodyValue("Validation error").block()));

        SaveUserDTO userDto = new SaveUserDTO(
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

/*
    @Test
    void testListenGETUseCase() {
        webTestClient.get()
                .uri("/api/usecase/path")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(userResponse -> {
                            Assertions.assertThat(userResponse).isEmpty();
                        }
                );
    }

    @Test
    void testListenGETOtherUseCase() {
        webTestClient.get()
                .uri("/api/otherusercase/path")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(userResponse -> {
                            Assertions.assertThat(userResponse).isEmpty();
                        }
                );
    }

    @Test
    void testListenPOSTUseCase() {
        webTestClient.post()
                .uri("/api/usecase/otherpath")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue("")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(userResponse -> {
                            Assertions.assertThat(userResponse).isEmpty();
                        }
                );
    }*/
}
