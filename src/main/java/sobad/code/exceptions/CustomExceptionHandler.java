package sobad.code.exceptions;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;


@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<AppError> entityNotFound(EntityNotFound e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(404)
                .body(new AppError(
                        HttpStatus.NOT_FOUND.value(),
                        e.getMessage(),
                        Instant.now().toString()
                ));
    }

    @ExceptionHandler
    public ResponseEntity<AppError> contextUserNotFound(ContextGetUserException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(403)
                .body(new AppError(
                        HttpStatus.FORBIDDEN.value(),
                        "В запросе не присутствует токен доступа, "
                                + "невозможно аутентифицировать текущего пользователя для выполнения запроса!",
                        Instant.now().toString()
                ));
    }

    @ExceptionHandler
    public ResponseEntity<AppError> selfReferenceRequest(SelfRequestException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(500)
                .body(new AppError(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        e.getMessage(),
                        Instant.now().toString()
                ));
    }

    @ExceptionHandler
    public ResponseEntity<AppError> friendshipException(FriendshipException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(422)
                .body(new AppError(
                        HttpStatus.FORBIDDEN.value(),
                        "Вы не являетесь друзьями с этим пользователем!",
                        Instant.now().toString()
                ));
    }

    @ExceptionHandler
    public ResponseEntity<AppError> authenticationException(UsernameNotFoundException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(401)
                .body(new AppError(
                        HttpStatus.UNAUTHORIZED.value(),
                        e.getMessage(),
                        Instant.now().toString()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<AppError> handleValidationExceptions(MethodArgumentNotValidException e) {
        List<String> errorMessages = e.getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage).toList();

        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(422)
                .body(new AppError(UNPROCESSABLE_ENTITY.value(), errorMessages.get(0), Instant.now().toString()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<AppError> validationException(ConstraintViolationException e) {
        List<String> errorMessages = e.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage).toList();
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(422)
                .body(new AppError(UNPROCESSABLE_ENTITY.value(), errorMessages.get(0), Instant.now().toString()));
    }

    @ExceptionHandler
    public ResponseEntity<AppError> runtimeError(RuntimeException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(500)
                .body(new AppError(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        e.getMessage(),
                        Instant.now().toString()
                ));
    }

}
