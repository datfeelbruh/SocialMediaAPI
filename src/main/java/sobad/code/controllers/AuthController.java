package sobad.code.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sobad.code.dtos.TokenDtoResponse;
import sobad.code.dtos.UserDtoRequest;
import sobad.code.services.AuthServiceImpl;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthServiceImpl authService;

    @PostMapping("")
    public ResponseEntity<TokenDtoResponse> authenticate(@RequestBody UserDtoRequest userDtoRequest) {
        return new ResponseEntity<>(authService.authenticateUser(userDtoRequest), OK);
    }
 }
