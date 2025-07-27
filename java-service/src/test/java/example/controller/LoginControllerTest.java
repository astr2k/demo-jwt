package example.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for {@link LoginController}
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LoginControllerTest {
    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    JwtDecoder jwtDecoder;

    MockMvc mvc;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    void when_LoginWithValidCredentials_then_SuccessfullyReturnsValidToken() throws Exception {
        MvcResult result = mvc.perform(post("/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                	{
                                		"login": "user2",
                                		"password": "pwd2"
                                	}
                                """))
                .andExpect(status().isOk())
                .andReturn();

        String token = result.getResponse().getContentAsString();
        Assertions.assertEquals("user2", jwtDecoder.decode(token).getClaims().get("sub"));
    }

    @Test
    void when_LoginWithWrongCredentials_then_Unauthorized() throws Exception {
        mvc.perform(post("/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                	{
                                		"login": "wrong",
                                		"password": "bad"
                                	}
                                """))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }
}