package co.com.pragma.authentication.api.exception;

import co.com.pragma.authentication.api.dto.GenericResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@WebFluxTest
@Import(GlobalExceptionHandler.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private WebTestClient webTestClient;

    // A test-specific endpoint to trigger exceptions
    @Configuration
    @RestController
    static class TestController {
        @GetMapping("/test/bad-request")
        public Mono<String> throwBadRequest() {
            return Mono.error(new IllegalArgumentException("Invalid input data"));
        }

        @GetMapping("/test/internal-error")
        public Mono<String> throwInternalError() {
            return Mono.error(new RuntimeException("Something went wrong"));
        }
    }

    @Test
    void testIllegalArgumentException_shouldReturnBadRequest() {
        webTestClient.get().uri("/test/bad-request")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(GenericResponseDto.class)
                .consumeWith(response -> {
                    GenericResponseDto<String> dto = response.getResponseBody();
                    assert dto != null;
                    assert dto.status() == HttpStatus.BAD_REQUEST.value();
                    assert dto.code().equals("ERROR");
                    assert dto.detail().equals("Invalid input data");
                });
    }

    @Test
    void testRuntimeException_shouldReturnInternalServerError() {
        webTestClient.get().uri("/test/internal-error")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(GenericResponseDto.class)
                .consumeWith(response -> {
                    GenericResponseDto<String> dto = response.getResponseBody();
                    assert dto != null;
                    assert dto.status() == HttpStatus.INTERNAL_SERVER_ERROR.value();
                    assert dto.code().equals("ERROR");
                    assert dto.detail().equals("Something went wrong");
                });
    }

}