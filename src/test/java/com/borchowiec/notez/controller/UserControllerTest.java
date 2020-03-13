package com.borchowiec.notez.controller;

import com.borchowiec.notez.model.User;
import com.borchowiec.notez.payload.UserInfoResponse;
import com.borchowiec.notez.repository.RoleRepository;
import com.borchowiec.notez.security.CustomUserDetailsService;
import com.borchowiec.notez.security.JwtAuthenticationEntryPoint;
import com.borchowiec.notez.security.JwtTokenProvider;
import com.borchowiec.notez.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import sun.security.acl.PrincipalImpl;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtTokenProvider tokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @MockBean
    private UserService userService;

    @BeforeEach
    void buildMvc() {
        mvc = standaloneSetup(new UserController(userService)).build();
    }

    @Test
    void getInfoAboutPrincipal() throws Exception {
        // given
        Principal principal = new PrincipalImpl("username");
        User user = new User(principal.getName(), "pass", "usr@gmail.com");
        user.setId(12L);

        // when
        when(userService.getUserOfUsernameOrThrowException(anyString())).thenReturn(user);
        ResultActions resultActions = mvc.perform(get("/user/me").principal(principal))
                .andDo(print())
                .andExpect(status().isOk());

        String responseAsString = resultActions.andReturn().getResponse().getContentAsString();
        UserInfoResponse response = objectMapper.readValue(responseAsString, UserInfoResponse.class);

        // then
        UserInfoResponse expected = new UserInfoResponse(user.getId(), user.getUsername(), user.getEmail());
        assertEquals(expected, response);

    }
}