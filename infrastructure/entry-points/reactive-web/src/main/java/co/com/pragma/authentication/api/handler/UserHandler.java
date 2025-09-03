package co.com.pragma.authentication.api.handler;

import co.com.pragma.authentication.api.dto.GenericResponseDto;
import co.com.pragma.authentication.api.dto.SaveUserRequestDTO;
import co.com.pragma.authentication.api.mapper.UserMapper;
import co.com.pragma.authentication.usecase.user.UserUseCase;
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
public class UserHandler {

    private final UserUseCase userUseCase;

    private final UserMapper userMapper;

    private final ValidationUtil validationUtil;

    public Mono<ServerResponse> listenGETByDocNumberUser(ServerRequest serverRequest) {
        log.info("Received GET request Find user by document number");

        String docNumber = serverRequest.pathVariable("docNumber");

        return userUseCase.findEmailUserByDocNumber(docNumber)
                .doOnSuccess(aVoid -> log.info("Finish find user by document number"))
                .map(userExistence -> GenericResponseDto.of(HttpStatus.OK.value(), "OK", userExistence))
                .flatMap(response -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response));
    }

    public Mono<ServerResponse> listenPOSTSaveUser(ServerRequest serverRequest) {
        log.info("Received POST request save user");
        return serverRequest.bodyToMono(SaveUserRequestDTO.class)
                .flatMap(validationUtil::validate)
                .map(userMapper::toModel)
                .flatMap(userUseCase::saveUser)
                .doOnSuccess(aVoid -> log.info("User saved successfully"))
                .thenReturn(GenericResponseDto.of(HttpStatus.OK.value(), "OK", "Saved successful"))
                .flatMap(response -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response));
    }

}
