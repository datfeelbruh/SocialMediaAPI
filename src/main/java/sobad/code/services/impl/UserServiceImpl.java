package sobad.code.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sobad.code.dtos.ResponseMessage;
import sobad.code.dtos.UserDtoRequest;
import sobad.code.dtos.UserDtoResponse;
//import sobad.code.entities.Friend;
//import sobad.code.entities.Friend;
import sobad.code.entities.FriendRequest;
import sobad.code.entities.Post;
import sobad.code.entities.User;
import sobad.code.repositories.FriendRequestRepository;
import sobad.code.repositories.UserRepository;
import sobad.code.services.UserService;
import sobad.code.status.FriendStatus;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final PasswordEncoder passwordEncoder;
//    private final UserRelationshipsServiceImpl userRelationshipsService;
//    private final MessageServiceImpl messageService;
    @Transactional
    public UserDtoResponse createUser(UserDtoRequest userDtoRequest) {
        User user = User.builder()
                .username(userDtoRequest.getUsername())
                .email(userDtoRequest.getEmail())
                .password(passwordEncoder.encode(userDtoRequest.getPassword()))
                .build();

        userRepository.save(user);

        return UserDtoResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    public UserDtoResponse getUser(Long id) {
        User user = userRepository.findById(id).orElseThrow();
        return new UserDtoResponse();
    }

    @Transactional
    public ResponseMessage sendFriendRequest(Long userId) {
        User currentUser = getCurrentUser();
        if (currentUser.getId().equals(userId)) {
            throw new RuntimeException("");
        }

        User userToFriend = userRepository.findById(userId).orElseThrow();
        FriendRequest friendRequest = FriendRequest.builder()
                .requester(currentUser)
                .friend(userToFriend)
                .status(FriendStatus.WAITING)
                .build();

        userToFriend.getFriendRequests().add(friendRequest);
        currentUser.getFollowers().add(userToFriend);
        userRepository.save(currentUser);
        userRepository.save(userToFriend);
        return ResponseMessage.builder()
                .message(FriendStatus.WAITING.getStatusMessage())
                .timestamp(Instant.now().toString())
                .build();
    }

//    public MessageDtoResponse sendMessage(Long userId, MessageDtoRequest messageDtoRequest) {
//        User currentUser = getCurrentUser();
//        if (currentUser.getId().equals(userId)) {
//            throw new RuntimeException("");
//        }
//
//        User userToSend = userRepository.findById(userId).orElseThrow();
//
//        if (!userRelationshipsService.isFriends(currentUser.getId(), userId)) {
//            throw new RuntimeException("");
//        }
//        return messageService.sendMessage(currentUser.getId(), userId, messageDtoRequest.getMessage());
//    }

    @Transactional
    public ResponseMessage decideFriendRequest(Long userId, FriendStatus requestStatus) {
        User currentUser = getCurrentUser();
        if (currentUser.getId().equals(userId)) {
            throw new RuntimeException("");
        }

        User userToFriend = userRepository.findById(userId).orElseThrow();
        Set<FriendRequest> friendRequest = currentUser.getFriendRequests();

//        FriendRequest friendRequest = currentUser.getFriendRequests()
//                .stream()
//                .filter(e -> e.getRequester().getId().equals(userToFriend.getId()))
//                .findFirst().orElseThrow();
        currentUser.getFriendRequests().remove(friendRequest);
        userRepository.save(currentUser);

        if (requestStatus.equals(FriendStatus.ACCEPTED)) {
            currentUser.getFriends().add(userToFriend);
            currentUser.getFollowers().add(userToFriend);
            userToFriend.getFriends().add(currentUser);
            userRepository.saveAll(List.of(currentUser, userToFriend));
        }

        return ResponseMessage.builder()
                .message(requestStatus.getStatusMessage())
                .timestamp(Instant.now().toString())
                .build();
    }
//
//    public ResponseMessage deleteFollow(Long userId) {
//        User currentUser = getCurrentUser();
//        if (currentUser.getId().equals(userId)) {
//            throw new RuntimeException("");
//        }
//
//        User userToFriend = userRepository.findById(userId).orElseThrow();
//        userRelationshipsService.removeFollower(currentUser);
//
//        return ResponseMessage.builder()
//                .message(FriendStatus.NO_FOLLOWS.getStatusMessage())
//                .timestamp(Instant.now().toString())
//                .build();
//    }
//
//    public ResponseMessage deleteFriend(Long userId) {
//        User currentUser = getCurrentUser();
//        if (currentUser.getId().equals(userId)) {
//            throw new RuntimeException("");
//        }
//
//        User userToFriend = userRepository.findById(userId).orElseThrow();
//        userRelationshipsService.removeFriend(currentUser, userToFriend);
//
//        return ResponseMessage.builder()
//                .message(FriendStatus.NO_FRIENDS.getStatusMessage())
//                .timestamp(Instant.now().toString())
//                .build();
//    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username);

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("USER"))
        );
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("Пользователь с данным username '%s' не найден", username)));
    }

    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден!"));
    }

    public void updateUserPosts(User user, Post post) {
        user.getPosts().add(post);
        userRepository.save(user);
    }
}
