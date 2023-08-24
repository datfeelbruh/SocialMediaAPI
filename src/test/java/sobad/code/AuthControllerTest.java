package sobad.code;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import sobad.code.dtos.TokenDtoResponse;
import sobad.code.dtos.UserDtoRequest;
import sobad.code.repositories.UserRepository;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static sobad.code.UserControllerTest.USER_CONTROLLER_PATH;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Slf4j
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void authWithExistedUser_ShouldReturnAuthToken() throws Exception {
        UserDtoRequest userDtoRequest = TestUtils.readJson(
                TestUtils.readFixture("users/user1.json"),
                new TypeReference<>() { }
        );

        MockHttpServletRequestBuilder request = post(USER_CONTROLLER_PATH)
                .content(TestUtils.writeJson(userDtoRequest))
                .contentType(APPLICATION_JSON);

        mockMvc.perform(request).andExpect(status().isCreated());

        MockHttpServletRequestBuilder authRequest = post("/auth")
                .content(TestUtils.writeJson(userDtoRequest))
                .contentType(APPLICATION_JSON);

        ResultActions resultActions = mockMvc.perform(authRequest).andExpect(status().isOk());
        String content = resultActions.andReturn().getResponse().getContentAsString(UTF_8);

        TokenDtoResponse tokenDtoResponse = TestUtils.readJson(content, new TypeReference<>() { });

        Assertions.assertNotNull(tokenDtoResponse);
    }

    @Test
    void authWithNonExistedUser_ShouldThrow() throws Exception {
        UserDtoRequest userDtoRequest = TestUtils.readJson(
                TestUtils.readFixture("users/user1.json"),
                new TypeReference<>() { }
        );

        UserDtoRequest userDtoRequest2 = TestUtils.readJson(
                TestUtils.readFixture("users/incorrectUser.json"),
                new TypeReference<>() { }
        );


        MockHttpServletRequestBuilder request = post(USER_CONTROLLER_PATH)
                .content(TestUtils.writeJson(userDtoRequest))
                .contentType(APPLICATION_JSON);

        mockMvc.perform(request).andExpect(status().isCreated());

        MockHttpServletRequestBuilder authRequest = post("/auth")
                .content(TestUtils.writeJson(userDtoRequest2))
                .contentType(APPLICATION_JSON);

        mockMvc.perform(authRequest).andExpect(status().isInternalServerError());
    }
}
