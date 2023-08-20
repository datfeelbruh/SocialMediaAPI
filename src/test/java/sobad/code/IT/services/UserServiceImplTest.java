package sobad.code.IT.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import sobad.code.CommonConfig;
import sobad.code.dtos.UserDtoRequest;
import sobad.code.dtos.UserDtoResponse;
import sobad.code.entities.User;
import sobad.code.repositories.FollowerRepository;
import sobad.code.repositories.FriendRepository;
import sobad.code.repositories.PostRepository;
import sobad.code.repositories.UserRepository;
import sobad.code.services.impl.UserServiceImpl;

@SpringJUnitConfig
@SpringBootTest(properties = {
        "jwt.secret=iufjvgdsbYGB23U4BKQB8CJHbjfdsbb82134b1bfdksjwb819234bfjhsbfj32b487b"
})
@ContextHierarchy({
        @ContextConfiguration(classes = CommonConfig.class),
        @ContextConfiguration(classes = UserServiceImplTestConfig.class)
})
class UserServiceImplTest {
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FollowerRepository followerRepository;
    @Autowired
    private FriendRepository friendRepository;


    @BeforeEach
    void beforeEach() {
        userRepository.deleteAll();
    }

    @Test
    void shouldCreateUser() {
        UserDtoRequest userDtoRequest = UserDtoRequest.builder()
                .username("user")
                .email("user@mail.com")
                .password("password")
                .build();

        UserDtoResponse userDtoResponse = userService.createUser(userDtoRequest);
        Assertions.assertNotNull(userDtoResponse);
        Assertions.assertNotEquals(userDtoRequest.getPassword(), userRepository.findAll().get(0).getPassword());
    }

    @Test
    @WithMockUser("user")
    void shouldCreateFollowAndFriendRequest() {
        UserDtoRequest userDtoRequest = UserDtoRequest.builder()
                .username("user")
                .email("user@mail.com")
                .password("password")
                .build();


        UserDtoRequest userDtoRequest1 = UserDtoRequest.builder()
                .username("user1")
                .email("user1@mail.com")
                .password("password")
                .build();

        userService.createUser(userDtoRequest);
        userService.createUser(userDtoRequest1);

        User user = userRepository.findById(2L).orElseThrow();
        userService.sendFriendRequest(user.getId());

    }
}
