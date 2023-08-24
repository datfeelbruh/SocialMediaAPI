package sobad.code.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sobad.code.dtos.TokenDtoResponse;
import sobad.code.dtos.UserDtoRequest;
import sobad.code.exceptions.AppError;
import sobad.code.services.impl.AuthServiceImpl;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "API аутентификации", description = "API для получения токена доступа к защищенным эндпоинтам.")
public class AuthController {
    private final AuthServiceImpl authService;
    @Operation(summary = "Получить токен", description = """
            Endpoint для получения JWT токена
            """)
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "JWT токен",
            content = {
                @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = TokenDtoResponse.class)
                        )
                }
            ),
        @ApiResponse(
            responseCode = "401",
            description = "Не удалось аутентификировать пользователя с такими данными.",
            content = {
                @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = AppError.class)
                        )
                }
            )
    })
    @PostMapping("")
    public ResponseEntity<TokenDtoResponse> authenticate(@RequestBody UserDtoRequest userDtoRequest) {
        return new ResponseEntity<>(authService.authenticateUser(userDtoRequest), OK);
    }
}
