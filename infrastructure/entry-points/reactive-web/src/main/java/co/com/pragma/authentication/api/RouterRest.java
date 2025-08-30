package co.com.pragma.authentication.api;

import co.com.pragma.authentication.api.config.UserPath;
import co.com.pragma.authentication.api.dto.GenericResponseDto;
import co.com.pragma.authentication.api.dto.SaveUserRequestDTO;
import co.com.pragma.authentication.model.user.UserExistence;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class RouterRest {

    private final UserPath userPath;

    @Bean
    @RouterOperations({
            // Operation for POST /users (your existing endpoint)
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
                                    content = @Content(schema = @Schema(implementation = SaveUserRequestDTO.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "User saved successfully"),
                                    @ApiResponse(responseCode = "400", description = "Bad request"),
                                    @ApiResponse(responseCode = "500", description = "Internal server error")
                            }
                    )),
            @RouterOperation(path = "/api/v1/usuarios/{docNumber}",
                    produces = "application/json",
                    method = GET,
                    operation = @Operation(
                            operationId = "findUserByDocument",
                            summary = "Find user existence by document number",
                            tags = {"Users"},
                            parameters = @Parameter(
                                    in = ParameterIn.PATH,
                                    name = "docNumber",
                                    description = "The document number of the user",
                                    required = true,
                                    schema = @Schema(type = "string")
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "User found",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = UserExistence.class),
                                                    examples = {
                                                            @ExampleObject(
                                                                    name = "User found",
                                                                    value = """
                                                                            {
                                                                                "found": true,
                                                                                "email": "test@example.com"
                                                                            }
                                                                            """
                                                            ),
                                                            @ExampleObject(
                                                                    name = "User not found",
                                                                    value = """
                                                                            {
                                                                                "found": false,
                                                                                "email": null
                                                                            }
                                                                            """
                                                            )
                                                    }
                                            )
                                    ),
                                    @ApiResponse(responseCode = "400", description = "Bad request"),
                                    @ApiResponse(responseCode = "404", description = "User not found"),
                                    @ApiResponse(responseCode = "500", description = "Internal server error")
                            }
                    ))
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST(userPath.getUsers()), handler::listenPOSTSaveUser)
                .andRoute(GET(userPath.getUserByDocument()), handler::listenGETByDocNumberUser);
    }

}
