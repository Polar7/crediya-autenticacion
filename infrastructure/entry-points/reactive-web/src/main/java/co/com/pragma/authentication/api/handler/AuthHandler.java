package co.com.pragma.authentication.api.handler;

import co.com.pragma.authentication.api.dto.GenericResponseDto;
import co.com.pragma.authentication.api.dto.LoginRequestDTO;
import co.com.pragma.authentication.api.dto.SaveUserRequestDTO;
import co.com.pragma.authentication.usecase.auth.AuthUseCase;
import co.com.pragma.authentication.validation.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthHandler {

    private final AuthUseCase authUseCase;

    private final ValidationUtil validationUtil;

    public Mono<ServerResponse> listenPOSTLogin(ServerRequest serverRequest) {
        log.info("Received POST request Login");

        return serverRequest.bodyToMono(LoginRequestDTO.class)
                .flatMap(validationUtil::validate)
                .flatMap(x -> authUseCase.login(x.email(), x.password()))
                .doOnSuccess(aVoid -> log.info("Login successfully"))
                .map(jwt -> GenericResponseDto.of(HttpStatus.OK.value(), "OK", jwt))
                .flatMap(response -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response));
    }

}
