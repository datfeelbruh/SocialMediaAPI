package sobad.code.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import sobad.code.dtos.PostDtoRequest;
import sobad.code.dtos.PostDtoResponse;
import sobad.code.exceptions.AppError;
import sobad.code.services.impl.PostServiceImpl;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@Tag(name = "API постов пользоватлей", description = "API CRUD операций над постами пользователей")
public class PostController {
    private final PostServiceImpl postService;

    @Operation(summary = "Создание нового поста", description = """
            Endpoint для создания нового поста
            """)
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Созданный пост.",
            content = {
                @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = PostDtoResponse.class)
                        )
                }
            ),
        @ApiResponse(
            responseCode = "500",
            description = "Не удалось создать пост.",
            content = {
                @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = AppError.class)
                        )
                }
            )
    })
    @PostMapping("")
    public ResponseEntity<PostDtoResponse> createPost(@RequestBody PostDtoRequest postDtoRequest) {
        return new ResponseEntity<>(postService.createPost(postDtoRequest), CREATED);
    }

    @Operation(summary = "Получить пост", description = """
            Endpoint для получения поста по его id
            """)
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Пост пользователя.",
            content = {
                @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = PostDtoResponse.class)
                        )
                }
            ),
        @ApiResponse(
            responseCode = "404",
            description = "Не удалось получить пост.",
            content = {
                @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = AppError.class)
                        )
                }
            )
    })
    @GetMapping("/{postId}")
    public ResponseEntity<PostDtoResponse> getPost(@PathVariable(value = "postId") Long postId) {
        return new ResponseEntity<>(postService.getPostDtoResponse(postId), OK);
    }

    @Operation(summary = "Получить пользовательскую ленту", description = """
            Endpoint для получения списка постов от друзей для текущего пользователя
            """)
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Список постов.",
            content = {
                @Content(
                        mediaType = "application/json",
                        array = @ArraySchema(schema = @Schema(implementation = PostDtoResponse.class))
                        )
                }
            ),
    })

    @GetMapping("/feed")
    public ResponseEntity<List<PostDtoResponse>> getFeed(
            @RequestParam(value = "limit", defaultValue = "10") Integer limit,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "sorted", defaultValue = "false") Boolean sorted) {
        return new ResponseEntity<>(postService.getFollowedUsersPost(page, limit, sorted), OK);
    }
    @Operation(summary = "Обновить данные поста", description = """
            Endpoint для обновления пользовательского поста
            """)
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Пост успешно обновлен.",
            content = {
                @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = PostDtoResponse.class)
                        )
                }
            ),
        @ApiResponse(
            responseCode = "404",
            description = "Не удалось обновить пост.",
            content = {
                @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = AppError.class)
                        )
                }
            )
    })
    @PutMapping("/{postId}")
    public ResponseEntity<PostDtoResponse> updatePost(@PathVariable(value = "postId") Long postId,
                                                      @RequestBody PostDtoRequest postDtoRequest) {
        return new ResponseEntity<>(postService.updatePost(postId, postDtoRequest), OK);
    }

    @Operation(summary = "Удалить пост", description = """
            Endpoint для удаление поста
            """)
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Удаленный пост",
            content = {
                @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = PostDtoResponse.class)
                        )
                }
            ),
        @ApiResponse(
            responseCode = "404",
            description = "Не удалось удалить пост.",
            content = {
                @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = AppError.class)
                        )
                }
            )
    })
    @DeleteMapping("/{postId}")
    public ResponseEntity<PostDtoResponse> deletePost(@PathVariable(value = "postId") Long postId) {
        return new ResponseEntity<>(postService.deletePost(postId), OK);
    }
}
