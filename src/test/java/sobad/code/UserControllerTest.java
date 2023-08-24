package sobad.code;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import sobad.code.dtos.UserDtoRequest;
import sobad.code.dtos.UserDtoResponse;
import sobad.code.entities.User;
import sobad.code.repositories.UserRepository;
import sobad.code.status.Status;


import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Slf4j
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    static final String USER_CONTROLLER_PATH = "/users";

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void createUser_WithCorrectData_ShouldReturnUserDtoResponse() throws Exception {
        UserDtoRequest userDtoRequest = TestUtils.readJson(
                TestUtils.readFixture("users/user1.json"),
                new TypeReference<>() { }
        );

        MockHttpServletRequestBuilder request = post(USER_CONTROLLER_PATH)
                .content(TestUtils.writeJson(userDtoRequest))
                .contentType(APPLICATION_JSON);

        ResultActions resultActions = mockMvc.perform(request).andExpect(status().isCreated());
        String content = resultActions.andReturn().getResponse().getContentAsString(UTF_8);
        UserDtoResponse response = TestUtils.readJson(content, new TypeReference<>() { });

        Assertions.assertThat(userRepository.findAll()).hasSize(1);
        Assertions.assertThat(response.getUsername()).isEqualTo(userDtoRequest.getUsername());
    }

    @Test
    void createUser_WithIncorrectData_ShouldNotCreateUser() throws Exception {
        UserDtoRequest userDtoRequest = TestUtils.readJson(
                TestUtils.readFixture("users/incorrectUser.json"),
                new TypeReference<>() { }
        );

        MockHttpServletRequestBuilder request = post(USER_CONTROLLER_PATH)
                .content(TestUtils.writeJson(userDtoRequest))
                .contentType(APPLICATION_JSON);

        mockMvc.perform(request).andExpect(status().isUnprocessableEntity());
        Assertions.assertThat(userRepository.findAll()).isEmpty();
    }

    @Test
    void sendFriendRequestToAnotherUser_ShouldAddFollow() throws Exception {
        UserDtoRequest userDtoRequest1 = TestUtils.readJson(
                TestUtils.readFixture("users/user1.json"),
                new TypeReference<>() { }
        );

        UserDtoRequest userDtoRequest2 = TestUtils.readJson(
                TestUtils.readFixture("users/user2.json"),
                new TypeReference<>() { }
        );

        MockHttpServletRequestBuilder requestCreateUser1 = post(USER_CONTROLLER_PATH)
                .content(TestUtils.writeJson(userDtoRequest1))
                .contentType(APPLICATION_JSON);

        MockHttpServletRequestBuilder requestCreateUser2 = post(USER_CONTROLLER_PATH)
                .content(TestUtils.writeJson(userDtoRequest2))
                .contentType(APPLICATION_JSON);

        mockMvc.perform(requestCreateUser1);
        mockMvc.perform(requestCreateUser2);

        Long userIdToRequest = userRepository.findByUsername(userDtoRequest2.getUsername()).get().getId();

        MockHttpServletRequestBuilder friendRequest = get(USER_CONTROLLER_PATH + "/friend/"
                + userIdToRequest)
                .with(user(userDtoRequest1.getUsername()).password(userDtoRequest1.getPassword()));

        mockMvc.perform(friendRequest).andExpect(status().isOk());

        User user1 = userRepository.findByUsername(userDtoRequest1.getUsername()).get();
        User user2 = userRepository.findByUsername(userDtoRequest2.getUsername()).get();

        Assertions.assertThat(user1.getFollowers()).hasSize(1);
        Assertions.assertThat(user2.getFriendRequests()).hasSize(1);
    }

    @Test
    void sendFriendRequestToSelf_ShouldThrow() throws Exception {
        UserDtoRequest userDtoRequest1 = TestUtils.readJson(
                TestUtils.readFixture("users/user1.json"),
                new TypeReference<>() { }
        );

        MockHttpServletRequestBuilder requestCreateUser1 = post(USER_CONTROLLER_PATH)
                .content(TestUtils.writeJson(userDtoRequest1))
                .contentType(APPLICATION_JSON);

        mockMvc.perform(requestCreateUser1);

        Long userIdToRequest = userRepository.findByUsername(userDtoRequest1.getUsername()).get().getId();

        MockHttpServletRequestBuilder friendRequest = get(USER_CONTROLLER_PATH + "/friend/"
                + userIdToRequest)
                .with(user(userDtoRequest1.getUsername()).password(userDtoRequest1.getPassword()));

        mockMvc.perform(friendRequest).andExpect(status().isInternalServerError());

        User user = userRepository.findByUsername(userDtoRequest1.getUsername()).get();

        Assertions.assertThat(user.getFollowers()).isEmpty();
        Assertions.assertThat(user.getFriendRequests()).isEmpty();
    }

    @Test
    @WithMockUser("dmitriy")
    void decideFriendRequestToAnotherUser_ShouldAddFriendship() throws Exception {
        UserDtoRequest userDtoRequest1 = TestUtils.readJson(
                TestUtils.readFixture("users/user1.json"),
                new TypeReference<>() { }
        );

        UserDtoRequest userDtoRequest2 = TestUtils.readJson(
                TestUtils.readFixture("users/user2.json"),
                new TypeReference<>() { }
        );

        MockHttpServletRequestBuilder requestCreateUser1 = post(USER_CONTROLLER_PATH)
                .content(TestUtils.writeJson(userDtoRequest1))
                .contentType(APPLICATION_JSON);

        MockHttpServletRequestBuilder requestCreateUser2 = post(USER_CONTROLLER_PATH)
                .content(TestUtils.writeJson(userDtoRequest2))
                .contentType(APPLICATION_JSON);

        mockMvc.perform(requestCreateUser1);
        mockMvc.perform(requestCreateUser2);

        Long userIdToRequest1 = userRepository.findByUsername(userDtoRequest1.getUsername()).get().getId();
        Long userIdToRequest2 = userRepository.findByUsername(userDtoRequest2.getUsername()).get().getId();

        MockHttpServletRequestBuilder friendRequest = get(USER_CONTROLLER_PATH + "/friend/"
                + userIdToRequest2)
                .with(user(userDtoRequest1.getUsername()).password(userDtoRequest1.getPassword()));

        mockMvc.perform(friendRequest).andExpect(status().isOk());

        MockHttpServletRequestBuilder decideFriendRequest = put(USER_CONTROLLER_PATH + "/friend/"
                + userIdToRequest1)
                .param("status", Status.ACCEPTED.toString())
                .with(user(userDtoRequest2.getUsername()).password(userDtoRequest2.getPassword()));

        mockMvc.perform(decideFriendRequest).andExpect(status().isOk());

        User user1 = userRepository.findByUsername(userDtoRequest1.getUsername()).get();
        User user2 = userRepository.findByUsername(userDtoRequest2.getUsername()).get();

        Assertions.assertThat(user1.getFollowers()).hasSize(1);
        Assertions.assertThat(user2.getFollowers()).hasSize(1);
        Assertions.assertThat(user1.getFriends()).hasSize(1);
        Assertions.assertThat(user2.getFriends()).hasSize(1);
    }

    @Test
    @WithMockUser("dmitriy")
    void decideFriendAcceptedRequestToAnotherUser_ShouldNotAddFriendship() throws Exception {
        UserDtoRequest userDtoRequest1 = TestUtils.readJson(
                TestUtils.readFixture("users/user1.json"),
                new TypeReference<>() { }
        );

        UserDtoRequest userDtoRequest2 = TestUtils.readJson(
                TestUtils.readFixture("users/user2.json"),
                new TypeReference<>() { }
        );

        MockHttpServletRequestBuilder requestCreateUser1 = post(USER_CONTROLLER_PATH)
                .content(TestUtils.writeJson(userDtoRequest1))
                .contentType(APPLICATION_JSON);

        MockHttpServletRequestBuilder requestCreateUser2 = post(USER_CONTROLLER_PATH)
                .content(TestUtils.writeJson(userDtoRequest2))
                .contentType(APPLICATION_JSON);

        mockMvc.perform(requestCreateUser1);
        mockMvc.perform(requestCreateUser2);

        Long userIdToRequest1 = userRepository.findByUsername(userDtoRequest1.getUsername()).get().getId();
        Long userIdToRequest2 = userRepository.findByUsername(userDtoRequest2.getUsername()).get().getId();

        MockHttpServletRequestBuilder friendRequest = get(USER_CONTROLLER_PATH + "/friend/"
                + userIdToRequest2)
                .with(user(userDtoRequest1.getUsername()).password(userDtoRequest1.getPassword()));

        mockMvc.perform(friendRequest).andExpect(status().isOk());

        MockHttpServletRequestBuilder decideFriendRequest = put(USER_CONTROLLER_PATH + "/friend/"
                + userIdToRequest1)
                .param("status", Status.DECLINED.toString())
                .with(user(userDtoRequest2.getUsername()).password(userDtoRequest2.getPassword()));

        mockMvc.perform(decideFriendRequest).andExpect(status().isOk());

        User user1 = userRepository.findByUsername(userDtoRequest1.getUsername()).get();
        User user2 = userRepository.findByUsername(userDtoRequest2.getUsername()).get();

        Assertions.assertThat(user1.getFollowers()).hasSize(1);
        Assertions.assertThat(user2.getFollowers()).isEmpty();
        Assertions.assertThat(user1.getFriends()).isEmpty();
        Assertions.assertThat(user2.getFriends()).isEmpty();
    }

    @Test
    void decideFriendRequestToSelf_ShouldThrow() throws Exception {
        UserDtoRequest userDtoRequest1 = TestUtils.readJson(
                TestUtils.readFixture("users/user1.json"),
                new TypeReference<>() { }
        );

        MockHttpServletRequestBuilder requestCreateUser1 = post(USER_CONTROLLER_PATH)
                .content(TestUtils.writeJson(userDtoRequest1))
                .contentType(APPLICATION_JSON);

        mockMvc.perform(requestCreateUser1);

        MockHttpServletRequestBuilder decideFriendRequest = put(USER_CONTROLLER_PATH + "/friend/1")
                .param("status", Status.DECLINED.toString())
                .with(user(userDtoRequest1.getUsername()).password(userDtoRequest1.getPassword()));

        mockMvc.perform(decideFriendRequest).andExpect(status().isInternalServerError());
    }

    @Test
    void deleteFriend_ShouldDeleteFriendAndUnfollow() throws Exception {
        UserDtoRequest userDtoRequest1 = TestUtils.readJson(
                TestUtils.readFixture("users/user1.json"),
                new TypeReference<>() { }
        );

        UserDtoRequest userDtoRequest2 = TestUtils.readJson(
                TestUtils.readFixture("users/user2.json"),
                new TypeReference<>() { }
        );

        MockHttpServletRequestBuilder requestCreateUser1 = post(USER_CONTROLLER_PATH)
                .content(TestUtils.writeJson(userDtoRequest1))
                .contentType(APPLICATION_JSON);

        MockHttpServletRequestBuilder requestCreateUser2 = post(USER_CONTROLLER_PATH)
                .content(TestUtils.writeJson(userDtoRequest2))
                .contentType(APPLICATION_JSON);

        mockMvc.perform(requestCreateUser1);
        mockMvc.perform(requestCreateUser2);

        Long userIdToRequest1 = userRepository.findByUsername(userDtoRequest1.getUsername()).get().getId();
        Long userIdToRequest2 = userRepository.findByUsername(userDtoRequest2.getUsername()).get().getId();

        MockHttpServletRequestBuilder friendRequest = get(USER_CONTROLLER_PATH + "/friend/"
                + userIdToRequest2)
                .with(user(userDtoRequest1.getUsername()).password(userDtoRequest1.getPassword()));

        mockMvc.perform(friendRequest).andExpect(status().isOk());

        MockHttpServletRequestBuilder decideFriendRequest = put(USER_CONTROLLER_PATH + "/friend/"
                + userIdToRequest1)
                .param("status", Status.ACCEPTED.toString())
                .with(user(userDtoRequest2.getUsername()).password(userDtoRequest2.getPassword()));

        mockMvc.perform(decideFriendRequest).andExpect(status().isOk());

        MockHttpServletRequestBuilder deleteRequest = delete(USER_CONTROLLER_PATH + "/friend/"
                + userIdToRequest2)
                .with(user(userDtoRequest1.getUsername()).password(userDtoRequest1.getPassword()));

        mockMvc.perform(deleteRequest).andExpect(status().isOk());

        User user1 = userRepository.findByUsername(userDtoRequest1.getUsername()).get();
        User user2 = userRepository.findByUsername(userDtoRequest2.getUsername()).get();

        Assertions.assertThat(user1.getFollowers()).isEmpty();
        Assertions.assertThat(user2.getFollowers()).hasSize(1);
        Assertions.assertThat(user1.getFriends()).isEmpty();
        Assertions.assertThat(user2.getFriends()).isEmpty();
    }
}
