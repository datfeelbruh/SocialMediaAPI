package sobad.code.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sobad.code.dtos.UserDtoRequest;
import sobad.code.dtos.UserDtoResponse;
import sobad.code.services.UserService;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping("")
    public ResponseEntity<UserDtoResponse> creteUser(@RequestBody UserDtoRequest userDtoRequest) {
        return new ResponseEntity<>(userService.createUser(userDtoRequest), CREATED);
    }
}
