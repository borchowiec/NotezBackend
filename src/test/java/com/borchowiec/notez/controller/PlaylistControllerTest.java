package com.borchowiec.notez.controller;

import com.borchowiec.notez.model.Playlist;
import com.borchowiec.notez.repository.RoleRepository;
import com.borchowiec.notez.repository.SongRepository;
import com.borchowiec.notez.repository.UserRepository;
import com.borchowiec.notez.security.CustomUserDetailsService;
import com.borchowiec.notez.security.JwtAuthenticationEntryPoint;
import com.borchowiec.notez.security.JwtTokenProvider;
import com.borchowiec.notez.service.PlaylistService;
import com.borchowiec.notez.service.SongService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlaylistController.class)
class PlaylistControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    AuthenticationManager authenticationManager;

    @MockBean
    private UserRepository userRepository;

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
    private PlaylistService playlistService;

    @Test
    void createPlaylist() throws Exception {
        mvc.perform(post("/playlist")
                .contentType("application/json")
                .content("{\"playlistName\": \"test\"}"))
                .andExpect(status().isOk());
    }
}