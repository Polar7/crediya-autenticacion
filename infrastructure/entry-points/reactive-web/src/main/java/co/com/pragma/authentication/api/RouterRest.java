package co.com.pragma.authentication.api;

import co.com.pragma.authentication.api.config.UserPath;
import co.com.pragma.authentication.api.dto.GenericResponseDto;
import co.com.pragma.authentication.api.dto.SaveUserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class RouterRest {

    private final UserPath userPath;

    @Bean
    @RouterOperations({
            @RouterOperation(path = "/api/v1/usuarios",
                    produces = "application/json",
                    method = POST,
                    operation = @Operation(
                            operationId = "saveUser",
                            summary = "Saves a new user",
                            tags = {"Users"},
                            requestBody = @RequestBody(
                                    description = "User details to save",
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = SaveUserDTO.class))
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "User saved successfully",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = GenericResponseDto.class),
                                                    examples = {
                                                            @ExampleObject(
                                                                    value = """
                                                                            {
                                                                                "status": 200,
                                                                                "code": "OK",
                                                                                "detail": "Saved successful"
                                                                            }
                                                                            """
                                                            )
                                                    }
                                            )
                                    ),
                                    @ApiResponse(responseCode = "400", description = "Bad request"),
                                    @ApiResponse(responseCode = "500", description = "Internal server error")
                            }
                    ))
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST(userPath.getUsers()), handler::listenPOSTUseCase);
    }

}
