package sobad.code.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sobad.code.dtos.MessageDtoRequest;
import sobad.code.dtos.MessageDtoResponse;
import sobad.code.dtos.ResponseMessage;
import sobad.code.dtos.UserDtoRequest;
import sobad.code.dtos.UserDtoResponse;
import sobad.code.exceptions.AppError;
import sobad.code.services.impl.UserServiceImpl;
import sobad.code.status.Status;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "Пользовательское API", description = "API CRUD операций над пользователями")
public class UserController {
    private final UserServiceImpl userService;

    @Operation(summary = "Создание нового пользователя", description = """
            Endpoint для создания нового пользователя
            """)
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Пользователь успешно зарегистрирован.",
            content = {
                @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = UserDtoResponse.class)
                        )
                }
            ),
        @ApiResponse(
            responseCode = "401",
            description = "Не удалось создать пользователя.",
            content = {
                @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = AppError.class)
                        )
                }
            )
    })
    @PostMapping("")
    public ResponseEntity<UserDtoResponse> createUser(@RequestBody @Valid UserDtoRequest userDtoRequest) {
        return new ResponseEntity<>(userService.createUser(userDtoRequest), CREATED);
    }

    @Operation(summary = "Отправить заявку в друзья другому пользователю", description = """
            Endpoint для отправки запроса дружбы другому пользователю. Так же подписывает вас на пользователя.
            """)
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Запрос дружбы успешно отправлен.",
            content = {
                @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseMessage.class)
                        )
                }
            ),
        @ApiResponse(
            responseCode = "500",
            description = "Не удалось отправить запрос дружбы.",
            content = {
                @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = AppError.class)
                        )
                }
            )
    })

    @GetMapping("/friend/{userId}")
    public ResponseEntity<ResponseMessage> sendFriendRequest(@PathVariable(value = "userId") Long userId) {
        return new ResponseEntity<>(userService.sendFriendRequest(userId), OK);
    }

    @Operation(summary = "Отправить сообщение пользователю у вас в друзьях", description = """
            Endpoint для отправки сообщения пользователю который является вашим другом.
            """)
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Отправленное сообщение",
            content = {
                @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseMessage.class)
                        )
                }
            ),
        @ApiResponse(
            responseCode = "422",
            description = "Не удалось отправить сообщение.",
            content = {
                @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = AppError.class)
                        )
                }
            )
    })
    @PostMapping("/friend/message/{userId}")
    public ResponseEntity<MessageDtoResponse> sendMessage(@PathVariable(value = "userId") Long userId,
                                                          @RequestBody MessageDtoRequest messageDtoRequest) {
        return new ResponseEntity<>(userService.sendMessage(userId, messageDtoRequest), CREATED);
    }

    @Operation(summary = "Получить пользовательскую переписку", description = """
            Endpoint для получение переписки с пользователем.
            """)
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Список сообщений с пользователем.",
            content = {
                @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseMessage.class)
                        )
                }
            )
    })
    @GetMapping("/friend/message/{userId}")
    public ResponseEntity<List<MessageDtoResponse>> getMessages(@PathVariable(value = "userId") Long userId) {
        return new ResponseEntity<>(userService.getMessageHistory(userId), OK);
    }

    @Operation(summary = "Ответить пользователю на запрос дружбы", description = """
            Endpoint для ответа на запрос дружбы от другого пользователя.
            """)
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Сообщение о статусе запроса на дружбу.",
            content = {
                @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseMessage.class)
                        )
                }
            ),
        @ApiResponse(
            responseCode = "500",
            description = "Не удалось ответить на запрос дружбы.",
            content = {
                @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = AppError.class)
                        )
                }
            )
    })
    @PutMapping("/friend/{userId}")
    public ResponseEntity<ResponseMessage> decideFriendRequest(@PathVariable(value = "userId") Long userId,
                                                               @RequestParam(value = "status") Status status) {
        return new ResponseEntity<>(userService.decideFriendRequest(userId, status), OK);
    }

    @Operation(summary = "Удалить пользователя из друзей", description = """
            Endpoint для удаления и отписки от пользователя
            """)
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Пользователь удален из друзей, и вы больше не подписаны на него.",
            content = {
                @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseMessage.class)
                        )
                }
            ),
        @ApiResponse(
            responseCode = "500",
            description = "Не удалось удалить пользователя из друзей.",
            content = {
                @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = AppError.class)
                        )
                }
            )
    })
    @DeleteMapping("/friend/{userId}")
    public ResponseEntity<ResponseMessage> deleteFriend(@PathVariable(value = "userId") Long userId) {
        return new ResponseEntity<>(userService.deleteFriend(userId), OK);
    }
}
