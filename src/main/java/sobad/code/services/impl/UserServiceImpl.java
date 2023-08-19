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
import sobad.code.entities.Friend;
import sobad.code.entities.Post;
import sobad.code.entities.User;
import sobad.code.repositories.UserRepository;
import sobad.code.services.UserService;
import sobad.code.status.FriendStatus;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRelationshipsServiceImpl userRelationshipsService;

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

    @Transactional
    public ResponseMessage sendFriendRequest(Long userId) {
        User currentUser = getCurrentUser();
        if (currentUser.getId().equals(userId)) {
            throw new RuntimeException("");
        }

        User userToFriend = userRepository.findById(userId).orElseThrow();
        userRelationshipsService.addFriendRequest(currentUser, userToFriend);

        return ResponseMessage.builder()
                .message(FriendStatus.WAITING.getStatusMessage())
                .timestamp(Instant.now().toString())
                .build();
    }

    @Transactional
    public ResponseMessage decideFriendRequest(Long userId, FriendStatus requestStatus) {
        User currentUser = getCurrentUser();
        if (currentUser.getId().equals(userId)) {
            throw new RuntimeException("");
        }

        User userToFriend = userRepository.findById(userId).orElseThrow();
        userRelationshipsService.decideFriendRequest(currentUser, userToFriend, requestStatus);

        return ResponseMessage.builder()
                .message(requestStatus.getStatusMessage())
                .timestamp(Instant.now().toString())
                .build();
    }

    public ResponseMessage deleteFollow(Long userId) {
        User currentUser = getCurrentUser();
        if (currentUser.getId().equals(userId)) {
            throw new RuntimeException("");
        }

        User userToFriend = userRepository.findById(userId).orElseThrow();
        userRelationshipsService.removeFollower(currentUser);

        return ResponseMessage.builder()
                .message(FriendStatus.NO_FOLLOWS.getStatusMessage())
                .timestamp(Instant.now().toString())
                .build();
    }

    public ResponseMessage deleteFriend(Long userId) {
        User currentUser = getCurrentUser();
        if (currentUser.getId().equals(userId)) {
            throw new RuntimeException("");
        }

        User userToFriend = userRepository.findById(userId).orElseThrow();
        userRelationshipsService.removeFriend(currentUser, userToFriend);

        return ResponseMessage.builder()
                .message(FriendStatus.NO_FRIENDS.getStatusMessage())
                .timestamp(Instant.now().toString())
                .build();
    }

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
