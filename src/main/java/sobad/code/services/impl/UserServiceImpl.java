package sobad.code.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sobad.code.dtos.MessageDtoRequest;
import sobad.code.dtos.MessageDtoResponse;
import sobad.code.dtos.ResponseMessage;
import sobad.code.dtos.UserDtoRequest;
import sobad.code.dtos.UserDtoResponse;
import sobad.code.entities.FriendRequest;
import sobad.code.entities.Message;
import sobad.code.entities.Post;
import sobad.code.entities.User;
import sobad.code.exceptions.ContextGetUserException;
import sobad.code.exceptions.FriendshipException;
import sobad.code.exceptions.SelfRequestException;
import sobad.code.repositories.FriendRequestRepository;
import sobad.code.repositories.MessageRepository;
import sobad.code.repositories.UserRepository;
import sobad.code.services.UserService;
import sobad.code.status.Status;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private final FriendRequestRepository requestRepository;
    private final MessageRepository messageRepository;
    private final PasswordEncoder passwordEncoder;

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
            throw new SelfRequestException(String.format("Попытка отправить запрос в друзья самому себе! "
                    + "ID в запросе: '%s'. ID текущего пользователя: '%s'!", userId, currentUser.getId()));
        }
        log.info(String.valueOf(userRepository.findAll().size()));
        User userToFriend = userRepository.findById(userId).orElseThrow();
        FriendRequest friendRequest = FriendRequest.builder()
                .requester(currentUser)
                .friend(userToFriend)
                .status(Status.WAITING)
                .build();

        updateRelationships(userToFriend, currentUser, friendRequest);

        return ResponseMessage.builder()
                .message(Status.WAITING.getStatusMessage())
                .timestamp(Instant.now().toString())
                .build();
    }


    @Transactional
    public ResponseMessage decideFriendRequest(Long userId, Status requestStatus) {
        User currentUser = getCurrentUser();
        if (currentUser.getId().equals(userId)) {
            throw new SelfRequestException(String.format("Попытка ответить на запрос в друзья самому себе! "
                    + "ID в запросе: '%s'. ID текущего пользователя: '%s'!", userId, currentUser.getId()));
        }

        User userToFriend = userRepository.findById(userId).orElseThrow();
        currentUser.setFriendRequests(
                currentUser.getFriendRequests()
                        .stream()
                        .filter(e -> !e.getRequester().getId().equals(userId))
                        .collect(Collectors.toSet())
        );

        userRepository.save(currentUser);

        if (requestStatus.equals(Status.ACCEPTED)) {
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

    public ResponseMessage deleteFriend(Long userId) {
        User currentUser = getCurrentUser();
        if (currentUser.getId().equals(userId)) {
            throw new SelfRequestException(String.format("Попытка удалить из друзей самого себя! "
                    + "ID в запросе: '%s'. ID текущего пользователя: '%s'!", userId, currentUser.getId()));
        }
        User user = userRepository.findById(userId).orElseThrow();

        currentUser.setFriends(
                currentUser.getFriends()
                        .stream()
                        .filter(e -> !e.getId().equals(userId))
                        .collect(Collectors.toSet())
        );
        user.setFriends(
                user.getFriends()
                        .stream()
                        .filter(e -> !e.getId().equals(currentUser.getId()))
                        .collect(Collectors.toSet())
        );

        deleteFollow(userId, currentUser);
        userRepository.save(currentUser);

        return ResponseMessage.builder()
                .message(Status.NO_FRIENDS.getStatusMessage())
                .timestamp(Instant.now().toString())
                .build();
    }

    public void deleteFollow(Long userId, User user) {
        user.setFollowers(
                user.getFollowers()
                        .stream()
                        .filter(e -> !e.getId().equals(userId))
                        .collect(Collectors.toSet())
        );
    }

    public MessageDtoResponse sendMessage(Long userId, MessageDtoRequest messageDtoRequest) {
        User currentUser = getCurrentUser();
        User user = userRepository.findById(userId).orElseThrow();

        if (!currentUser.getFriends().contains(user)) {
            throw new FriendshipException();
        }

        Message message = Message.builder()
                .fromUser(currentUser)
                .toUser(user)
                .messageBody(messageDtoRequest.getMessage())
                .timestamp(Instant.now())
                .build();

        currentUser.getMessages().add(message);
        user.getMessages().add(message);

        userRepository.saveAll(List.of(currentUser, user));
        messageRepository.save(message);

        return MessageDtoResponse.builder()
                .from(message.getFromUser().getUsername())
                .to(message.getToUser().getUsername())
                .message(message.getMessageBody())
                .timestamp(message.getTimestamp().toString())
                .build();
    }

    public List<MessageDtoResponse> getMessageHistory(Long userId) {
        User currentUser = getCurrentUser();

        List<Message> messages = currentUser.getMessages()
                .stream()
                .filter(e -> e.getToUser().getId().equals(userId) || e.getFromUser().getId().equals(userId))
                .toList();

        return messages.stream()
                .map(e -> MessageDtoResponse.builder()
                        .from(e.getFromUser().getUsername())
                        .to(e.getToUser().getUsername())
                        .message(e.getMessageBody())
                        .timestamp(e.getTimestamp().toString())
                        .build())
                .toList();
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
                .orElseThrow(ContextGetUserException::new);
    }

    public void updateUserPosts(User user, Post post) {
        user.getPosts().add(post);
        userRepository.save(user);
    }

    @Transactional
    private void updateRelationships(User userToFriend, User currentUser, FriendRequest friendRequest) {
        userToFriend.getFriendRequests().add(friendRequest);
        friendRequest.setRequester(currentUser);
        friendRequest.setFriend(userToFriend);
        requestRepository.save(friendRequest);
        currentUser.getFollowers().add(userToFriend);
        userRepository.save(currentUser);
        userRepository.save(userToFriend);
    }
}
