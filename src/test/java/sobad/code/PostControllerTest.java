package sobad.code;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import sobad.code.dtos.PostDtoRequest;
import sobad.code.dtos.PostDtoResponse;
import sobad.code.dtos.UserDtoRequest;
import sobad.code.entities.Post;
import sobad.code.repositories.PostRepository;
import sobad.code.repositories.UserRepository;

import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static sobad.code.UserControllerTest.USER_CONTROLLER_PATH;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Slf4j
class PostControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;

    private static final String POST_CONTROLLER_PATH = "/posts";

    @BeforeEach
    void setUp() {
        postRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void createPost_ShouldReturnStatusOkAndPostDtoResponse() throws Exception {
        UserDtoRequest userDtoRequest = TestUtils.readJson(
                TestUtils.readFixture("users/user1.json"),
                new TypeReference<>() { }
        );

        MockHttpServletRequestBuilder request = post(USER_CONTROLLER_PATH)
                .content(TestUtils.writeJson(userDtoRequest))
                .contentType(APPLICATION_JSON);

        mockMvc.perform(request).andExpect(status().isCreated());

        PostDtoRequest postDtoRequest = TestUtils.readJson(
                TestUtils.readFixture("posts/post1.json"),
                new TypeReference<>() { }
        );

        MockHttpServletRequestBuilder createPostRequest = post(POST_CONTROLLER_PATH)
                .content(TestUtils.writeJson(postDtoRequest))
                .contentType(APPLICATION_JSON)
                .with(user(userDtoRequest.getUsername()).password(userDtoRequest.getPassword()));

        ResultActions resultActions = mockMvc.perform(createPostRequest).andExpect(status().isCreated());
        String content = resultActions.andReturn().getResponse().getContentAsString(UTF_8);

        PostDtoResponse postDtoResponse = TestUtils.readJson(content, new TypeReference<>() { });

        Assertions.assertThat(postDtoResponse).isNotNull();
    }

    @Test
    void getPostWithExistedId_ShouldReturnPostDtoResponse() throws Exception {
        UserDtoRequest userDtoRequest = TestUtils.readJson(
                TestUtils.readFixture("users/user1.json"),
                new TypeReference<>() { }
        );

        MockHttpServletRequestBuilder request = post(USER_CONTROLLER_PATH)
                .content(TestUtils.writeJson(userDtoRequest))
                .contentType(APPLICATION_JSON);

        mockMvc.perform(request).andExpect(status().isCreated());

        PostDtoRequest postDtoRequest = TestUtils.readJson(
                TestUtils.readFixture("posts/post1.json"),
                new TypeReference<>() { }
        );

        MockHttpServletRequestBuilder createPostRequest = post(POST_CONTROLLER_PATH)
                .content(TestUtils.writeJson(postDtoRequest))
                .contentType(APPLICATION_JSON)
                .with(user(userDtoRequest.getUsername()).password(userDtoRequest.getPassword()));

        mockMvc.perform(createPostRequest).andExpect(status().isCreated());

        Post post = postRepository.findAll().get(0);

        MockHttpServletRequestBuilder getPost = get(POST_CONTROLLER_PATH + "/" + post.getId())
                .with(user(userDtoRequest.getUsername()).password(userDtoRequest.getPassword()));

        ResultActions resultActions = mockMvc.perform(getPost).andExpect(status().isOk());
        String content = resultActions.andReturn().getResponse().getContentAsString(UTF_8);

        PostDtoResponse postDtoResponse = TestUtils.readJson(content, new TypeReference<>() { });

        Assertions.assertThat(postDtoResponse).isNotNull();
        Assertions.assertThat(postDtoResponse.getTitle()).isEqualTo(post.getTitle());
    }

    @Test
    void updatePostWithExistedId_ShouldReturnPostDtoResponse() throws Exception {
        UserDtoRequest userDtoRequest = TestUtils.readJson(
                TestUtils.readFixture("users/user1.json"),
                new TypeReference<>() { }
        );

        MockHttpServletRequestBuilder request = post(USER_CONTROLLER_PATH)
                .content(TestUtils.writeJson(userDtoRequest))
                .contentType(APPLICATION_JSON);

        mockMvc.perform(request).andExpect(status().isCreated());

        PostDtoRequest postDtoRequest = TestUtils.readJson(
                TestUtils.readFixture("posts/post1.json"),
                new TypeReference<>() { }
        );

        MockHttpServletRequestBuilder createPostRequest = post(POST_CONTROLLER_PATH)
                .content(TestUtils.writeJson(postDtoRequest))
                .contentType(APPLICATION_JSON)
                .with(user(userDtoRequest.getUsername()).password(userDtoRequest.getPassword()));

        mockMvc.perform(createPostRequest).andExpect(status().isCreated());

        Post post = postRepository.findAll().get(0);

        PostDtoRequest postDtoRequest1 = TestUtils.readJson(
                TestUtils.readFixture("posts/post2.json"),
                new TypeReference<>() { }
        );

        MockHttpServletRequestBuilder updatePostRequest = put(POST_CONTROLLER_PATH + "/" + post.getId())
                .content(TestUtils.writeJson(postDtoRequest1))
                .contentType(APPLICATION_JSON)
                .with(user(userDtoRequest.getUsername()).password(userDtoRequest.getPassword()));

        ResultActions resultActions = mockMvc.perform(updatePostRequest).andExpect(status().isOk());
        String content = resultActions.andReturn().getResponse().getContentAsString(UTF_8);
        PostDtoResponse postDtoResponse = TestUtils.readJson(content, new TypeReference<>() { });
        post = postRepository.findAll().get(0);

        Assertions.assertThat(postDtoResponse).isNotNull();
        Assertions.assertThat(postDtoResponse.getTitle()).isEqualTo(post.getTitle());
    }

    @Test
    void deletePost_ShouldReturnStatusOkAndDeletePostFromRepository() throws Exception {
        UserDtoRequest userDtoRequest = TestUtils.readJson(
                TestUtils.readFixture("users/user1.json"),
                new TypeReference<>() { }
        );

        MockHttpServletRequestBuilder request = post(USER_CONTROLLER_PATH)
                .content(TestUtils.writeJson(userDtoRequest))
                .contentType(APPLICATION_JSON);

        mockMvc.perform(request).andExpect(status().isCreated());

        PostDtoRequest postDtoRequest = TestUtils.readJson(
                TestUtils.readFixture("posts/post1.json"),
                new TypeReference<>() { }
        );

        MockHttpServletRequestBuilder createPostRequest = post(POST_CONTROLLER_PATH)
                .content(TestUtils.writeJson(postDtoRequest))
                .contentType(APPLICATION_JSON)
                .with(user(userDtoRequest.getUsername()).password(userDtoRequest.getPassword()));

        mockMvc.perform(createPostRequest).andExpect(status().isCreated());

        Post post = postRepository.findAll().get(0);
        MockHttpServletRequestBuilder deletePostRequest = delete(POST_CONTROLLER_PATH + "/" + post.getId())
                .with(user(userDtoRequest.getUsername()).password(userDtoRequest.getPassword()));

        mockMvc.perform(deletePostRequest).andExpect(status().isOk());

        Assertions.assertThat(postRepository.findAll()).isEmpty();
    }

    @Test
    void getFeed_ShouldReturnOnlyFollowerPosts() throws Exception {
        // reg users
        UserDtoRequest userDtoRequest1 = TestUtils.readJson(
                TestUtils.readFixture("users/user1.json"),
                new TypeReference<>() { }
        );

        UserDtoRequest userDtoRequest2 = TestUtils.readJson(
                TestUtils.readFixture("users/user2.json"),
                new TypeReference<>() { }
        );

        UserDtoRequest userDtoRequest3 = TestUtils.readJson(
                TestUtils.readFixture("users/user3.json"),
                new TypeReference<>() { }
        );

        MockHttpServletRequestBuilder requestCreateUser1 = post(USER_CONTROLLER_PATH)
                .content(TestUtils.writeJson(userDtoRequest1))
                .contentType(APPLICATION_JSON);

        MockHttpServletRequestBuilder requestCreateUser2 = post(USER_CONTROLLER_PATH)
                .content(TestUtils.writeJson(userDtoRequest2))
                .contentType(APPLICATION_JSON);

        MockHttpServletRequestBuilder requestCreateUser3 = post(USER_CONTROLLER_PATH)
                .content(TestUtils.writeJson(userDtoRequest3))
                .contentType(APPLICATION_JSON);

        mockMvc.perform(requestCreateUser1);
        mockMvc.perform(requestCreateUser2);
        mockMvc.perform(requestCreateUser3);

        // follow user1 on user2
        Long userIdToRequest = userRepository.findByUsername(userDtoRequest2.getUsername()).get().getId();

        MockHttpServletRequestBuilder friendRequest = get(USER_CONTROLLER_PATH + "/friend/"
                + userIdToRequest)
                .with(user(userDtoRequest1.getUsername()).password(userDtoRequest1.getPassword()));

        mockMvc.perform(friendRequest).andExpect(status().isOk());

        // user2 create post
        PostDtoRequest postDtoRequest = TestUtils.readJson(
                TestUtils.readFixture("posts/post1.json"),
                new TypeReference<>() { }
        );

        MockHttpServletRequestBuilder createPostRequest = post(POST_CONTROLLER_PATH)
                .content(TestUtils.writeJson(postDtoRequest))
                .contentType(APPLICATION_JSON)
                .with(user(userDtoRequest2.getUsername()).password(userDtoRequest2.getPassword()));

        mockMvc.perform(createPostRequest).andExpect(status().isCreated());

        // user3 create post
        PostDtoRequest postDtoRequest2 = TestUtils.readJson(
                TestUtils.readFixture("posts/post1.json"),
                new TypeReference<>() { }
        );

        MockHttpServletRequestBuilder createPostRequest2 = post(POST_CONTROLLER_PATH)
                .content(TestUtils.writeJson(postDtoRequest2))
                .contentType(APPLICATION_JSON)
                .with(user(userDtoRequest3.getUsername()).password(userDtoRequest3.getPassword()));

        mockMvc.perform(createPostRequest2).andExpect(status().isCreated());

        // user1 get his feed
        MockHttpServletRequestBuilder getFeedRequest = get(POST_CONTROLLER_PATH + "/feed")
                .with(user(userDtoRequest1.getUsername()).password(userDtoRequest1.getPassword()));

        ResultActions resultActions = mockMvc.perform(getFeedRequest).andExpect(status().isOk());

        String content = resultActions.andReturn().getResponse().getContentAsString(UTF_8);

        List<PostDtoResponse> postDtoResponse = TestUtils.readJson(content, new TypeReference<>() { });

        Assertions.assertThat(postDtoResponse)
                .isNotNull()
                .hasSize(1);
        Assertions.assertThat(postDtoResponse.get(0).getTitle()).isEqualTo(postDtoRequest.getTitle());
    }

    @Test
    void getFeedWithLimit1_ShouldReturnOnePost() throws Exception {
        // reg users
        UserDtoRequest userDtoRequest1 = TestUtils.readJson(
                TestUtils.readFixture("users/user1.json"),
                new TypeReference<>() { }
        );

        UserDtoRequest userDtoRequest2 = TestUtils.readJson(
                TestUtils.readFixture("users/user2.json"),
                new TypeReference<>() { }
        );

        UserDtoRequest userDtoRequest3 = TestUtils.readJson(
                TestUtils.readFixture("users/user3.json"),
                new TypeReference<>() { }
        );

        MockHttpServletRequestBuilder requestCreateUser1 = post(USER_CONTROLLER_PATH)
                .content(TestUtils.writeJson(userDtoRequest1))
                .contentType(APPLICATION_JSON);

        MockHttpServletRequestBuilder requestCreateUser2 = post(USER_CONTROLLER_PATH)
                .content(TestUtils.writeJson(userDtoRequest2))
                .contentType(APPLICATION_JSON);

        MockHttpServletRequestBuilder requestCreateUser3 = post(USER_CONTROLLER_PATH)
                .content(TestUtils.writeJson(userDtoRequest3))
                .contentType(APPLICATION_JSON);

        mockMvc.perform(requestCreateUser1);
        mockMvc.perform(requestCreateUser2);
        mockMvc.perform(requestCreateUser3);

        // follow user1 on user2
        Long userIdToRequest = userRepository.findByUsername(userDtoRequest2.getUsername()).get().getId();

        MockHttpServletRequestBuilder friendRequest = get(USER_CONTROLLER_PATH + "/friend/"
                + userIdToRequest)
                .with(user(userDtoRequest1.getUsername()).password(userDtoRequest1.getPassword()));

        mockMvc.perform(friendRequest).andExpect(status().isOk());

        // follow user1 on user3
        Long userIdToRequest3 = userRepository.findByUsername(userDtoRequest3.getUsername()).get().getId();

        MockHttpServletRequestBuilder friendRequest1 = get(USER_CONTROLLER_PATH + "/friend/"
                + userIdToRequest3)
                .with(user(userDtoRequest1.getUsername()).password(userDtoRequest1.getPassword()));

        mockMvc.perform(friendRequest1).andExpect(status().isOk());

        // user2 create post
        PostDtoRequest postDtoRequest = TestUtils.readJson(
                TestUtils.readFixture("posts/post1.json"),
                new TypeReference<>() { }
        );

        MockHttpServletRequestBuilder createPostRequest = post(POST_CONTROLLER_PATH)
                .content(TestUtils.writeJson(postDtoRequest))
                .contentType(APPLICATION_JSON)
                .with(user(userDtoRequest2.getUsername()).password(userDtoRequest2.getPassword()));

        mockMvc.perform(createPostRequest).andExpect(status().isCreated());

        // user3 create post
        PostDtoRequest postDtoRequest2 = TestUtils.readJson(
                TestUtils.readFixture("posts/post1.json"),
                new TypeReference<>() { }
        );

        MockHttpServletRequestBuilder createPostRequest2 = post(POST_CONTROLLER_PATH)
                .content(TestUtils.writeJson(postDtoRequest2))
                .contentType(APPLICATION_JSON)
                .with(user(userDtoRequest3.getUsername()).password(userDtoRequest3.getPassword()));

        mockMvc.perform(createPostRequest2).andExpect(status().isCreated());

        // user1 get his feed without limit
        MockHttpServletRequestBuilder getFeedRequest = get(POST_CONTROLLER_PATH + "/feed")
                .with(user(userDtoRequest1.getUsername()).password(userDtoRequest1.getPassword()));

        ResultActions resultActions = mockMvc.perform(getFeedRequest).andExpect(status().isOk());

        String content = resultActions.andReturn().getResponse().getContentAsString(UTF_8);

        List<PostDtoResponse> postDtoResponse = TestUtils.readJson(content, new TypeReference<>() { });

        Assertions.assertThat(postDtoResponse)
                .isNotNull()
                .hasSize(2);

        // user1 get his feed with limit
        MockHttpServletRequestBuilder getFeedRequest1 = get(POST_CONTROLLER_PATH + "/feed")
                .param("limit", "1")
                .with(user(userDtoRequest1.getUsername()).password(userDtoRequest1.getPassword()));

        ResultActions resultActions1 = mockMvc.perform(getFeedRequest1).andExpect(status().isOk());

        String content1 = resultActions1.andReturn().getResponse().getContentAsString(UTF_8);

        List<PostDtoResponse> postDtoResponse1 = TestUtils.readJson(content1, new TypeReference<>() { });

        Assertions.assertThat(postDtoResponse1)
                .isNotNull()
                .hasSize(1);

        Assertions.assertThat(postDtoResponse1.get(0).getTitle()).isEqualTo(postDtoRequest.getTitle());
    }

    @Test
    void getFeedWithLimit1AndPage2_ShouldReturnOnePostOnPage2() throws Exception {
        // reg users
        UserDtoRequest userDtoRequest1 = TestUtils.readJson(
                TestUtils.readFixture("users/user1.json"),
                new TypeReference<>() { }
        );

        UserDtoRequest userDtoRequest2 = TestUtils.readJson(
                TestUtils.readFixture("users/user2.json"),
                new TypeReference<>() { }
        );

        UserDtoRequest userDtoRequest3 = TestUtils.readJson(
                TestUtils.readFixture("users/user3.json"),
                new TypeReference<>() { }
        );

        MockHttpServletRequestBuilder requestCreateUser1 = post(USER_CONTROLLER_PATH)
                .content(TestUtils.writeJson(userDtoRequest1))
                .contentType(APPLICATION_JSON);

        MockHttpServletRequestBuilder requestCreateUser2 = post(USER_CONTROLLER_PATH)
                .content(TestUtils.writeJson(userDtoRequest2))
                .contentType(APPLICATION_JSON);

        MockHttpServletRequestBuilder requestCreateUser3 = post(USER_CONTROLLER_PATH)
                .content(TestUtils.writeJson(userDtoRequest3))
                .contentType(APPLICATION_JSON);

        mockMvc.perform(requestCreateUser1);
        mockMvc.perform(requestCreateUser2);
        mockMvc.perform(requestCreateUser3);

        // follow user1 on user2
        Long userIdToRequest = userRepository.findByUsername(userDtoRequest2.getUsername()).get().getId();

        MockHttpServletRequestBuilder friendRequest = get(USER_CONTROLLER_PATH + "/friend/"
                + userIdToRequest)
                .with(user(userDtoRequest1.getUsername()).password(userDtoRequest1.getPassword()));

        mockMvc.perform(friendRequest).andExpect(status().isOk());

        // follow user1 on user3
        Long userIdToRequest3 = userRepository.findByUsername(userDtoRequest3.getUsername()).get().getId();

        MockHttpServletRequestBuilder friendRequest1 = get(USER_CONTROLLER_PATH + "/friend/"
                + userIdToRequest3)
                .with(user(userDtoRequest1.getUsername()).password(userDtoRequest1.getPassword()));

        mockMvc.perform(friendRequest1).andExpect(status().isOk());

        // user2 create post
        PostDtoRequest postDtoRequest = TestUtils.readJson(
                TestUtils.readFixture("posts/post1.json"),
                new TypeReference<>() { }
        );

        MockHttpServletRequestBuilder createPostRequest = post(POST_CONTROLLER_PATH)
                .content(TestUtils.writeJson(postDtoRequest))
                .contentType(APPLICATION_JSON)
                .with(user(userDtoRequest2.getUsername()).password(userDtoRequest2.getPassword()));

        mockMvc.perform(createPostRequest).andExpect(status().isCreated());

        // user3 create post
        PostDtoRequest postDtoRequest2 = TestUtils.readJson(
                TestUtils.readFixture("posts/post1.json"),
                new TypeReference<>() { }
        );

        MockHttpServletRequestBuilder createPostRequest2 = post(POST_CONTROLLER_PATH)
                .content(TestUtils.writeJson(postDtoRequest2))
                .contentType(APPLICATION_JSON)
                .with(user(userDtoRequest3.getUsername()).password(userDtoRequest3.getPassword()));

        mockMvc.perform(createPostRequest2).andExpect(status().isCreated());

        // user1 get his feed without limit
        MockHttpServletRequestBuilder getFeedRequest = get(POST_CONTROLLER_PATH + "/feed")
                .with(user(userDtoRequest1.getUsername()).password(userDtoRequest1.getPassword()));

        ResultActions resultActions = mockMvc.perform(getFeedRequest).andExpect(status().isOk());

        String content = resultActions.andReturn().getResponse().getContentAsString(UTF_8);

        List<PostDtoResponse> postDtoResponse = TestUtils.readJson(content, new TypeReference<>() { });

        Assertions.assertThat(postDtoResponse)
                .isNotNull()
                .hasSize(2);

        // user1 get his feed with limit and pagination
        MockHttpServletRequestBuilder getFeedRequest1 = get(POST_CONTROLLER_PATH + "/feed")
                .param("limit", "1")
                .param("page", "2")
                .with(user(userDtoRequest1.getUsername()).password(userDtoRequest1.getPassword()));

        ResultActions resultActions1 = mockMvc.perform(getFeedRequest1).andExpect(status().isOk());

        String content1 = resultActions1.andReturn().getResponse().getContentAsString(UTF_8);

        List<PostDtoResponse> postDtoResponse1 = TestUtils.readJson(content1, new TypeReference<>() { });

        Assertions.assertThat(postDtoResponse1)
                .isNotNull()
                .hasSize(1);

        Assertions.assertThat(postDtoResponse1.get(0).getTitle()).isEqualTo(postDtoRequest2.getTitle());
    }
}
