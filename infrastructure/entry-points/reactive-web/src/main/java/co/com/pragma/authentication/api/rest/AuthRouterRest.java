package co.com.pragma.authentication.api.rest;

import co.com.pragma.authentication.api.config.AuthPath;
import co.com.pragma.authentication.api.dto.GenericResponseDto;
import co.com.pragma.authentication.api.dto.LoginRequestDTO;
import co.com.pragma.authentication.api.handler.AuthHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class AuthRouterRest {

    private final AuthPath authPath;

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/autenticacion",
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    method = RequestMethod.POST,
                    beanClass = AuthHandler.class,
                    beanMethod = "listenPOSTLogin",
                    operation = @Operation(
                            operationId = "login",
                            summary = "User login and token generation",
                            tags = {"Authentication"},
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = LoginRequestDTO.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Login successful",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = GenericResponseDto.class)
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Bad Request (e.g., missing fields)"
                                    ),
                                    @ApiResponse(
                                            responseCode = "500",
                                            description = "Internal Server Error"
                                    )
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> authRouterFunction(AuthHandler authHandler) {
        return route(POST(authPath.getAuth()), authHandler::listenPOSTLogin);
    }

}
