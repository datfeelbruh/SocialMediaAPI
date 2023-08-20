package sobad.code.controllers;

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
import sobad.code.services.impl.UserServiceImpl;
import sobad.code.status.FriendStatus;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserServiceImpl userService;

//    @GetMapping("")
//    public ResponseEntity<ResponseMessage> getUser(@PathVariable(value = "userId") Long userId) {
//        return new ResponseEntity<>(userService.get(userId), CREATED);
//    }

    @PostMapping("")
    public ResponseEntity<UserDtoResponse> createUser(@RequestBody UserDtoRequest userDtoRequest) {
        return new ResponseEntity<>(userService.createUser(userDtoRequest), CREATED);
    }

    @PostMapping("/friend/{userId}")
    public ResponseEntity<ResponseMessage> sendFriendRequest(@PathVariable(value = "userId") Long userId) {
        return new ResponseEntity<>(userService.sendFriendRequest(userId), CREATED);
    }

//    @PostMapping("/friend/message/{userId}")
//    public ResponseEntity<MessageDtoResponse> sendMessage(@PathVariable(value = "userId") Long userId,
//                                                       @RequestBody MessageDtoRequest messageDtoRequest) {
//        return new ResponseEntity<>(userService.sendMessage(userId, messageDtoRequest), CREATED);
//    }

    @PutMapping("/friend/{userId}")
    public ResponseEntity<ResponseMessage> decideFriendRequest(@PathVariable(value = "userId") Long userId,
                                                               @RequestParam(value = "status") FriendStatus status) {
        return new ResponseEntity<>(userService.decideFriendRequest(userId, status), OK);
    }
//
//    @DeleteMapping("/friend/{userId}")
//    public ResponseEntity<ResponseMessage> deleteFriend(@PathVariable(value = "userId") Long userId) {
//        return new ResponseEntity<>(userService.deleteFriend(userId), OK);
//    }
//
//    @DeleteMapping("/follow/{userId}")
//    public ResponseEntity<ResponseMessage> deleteFollow(@PathVariable(value = "userId") Long userId) {
//        return new ResponseEntity<>(userService.deleteFollow(userId), OK);
//    }
}
