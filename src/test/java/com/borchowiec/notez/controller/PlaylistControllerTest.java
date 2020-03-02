package com.borchowiec.notez.controller;

import com.borchowiec.notez.model.Playlist;
import com.borchowiec.notez.model.User;
import com.borchowiec.notez.repository.PlaylistRepository;
import com.borchowiec.notez.repository.RoleRepository;
import com.borchowiec.notez.repository.UserRepository;
import com.borchowiec.notez.security.CustomUserDetailsService;
import com.borchowiec.notez.security.JwtAuthenticationEntryPoint;
import com.borchowiec.notez.security.JwtTokenProvider;
import com.borchowiec.notez.service.PlaylistService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import sun.security.acl.PrincipalImpl;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@WebMvcTest(PlaylistController.class)
class PlaylistControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private AuthenticationManager authenticationManager;

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

    @MockBean
    private PlaylistRepository playlistRepository;

    @Test
    void createPlaylist() throws Exception {
        mvc = standaloneSetup(new PlaylistController(userRepository, playlistService, playlistRepository))
                .build();

        when(userRepository.findByUsername(any())).thenReturn(Optional.of(new User()));

        mvc.perform(post("/playlist")
                .principal(new PrincipalImpl("principal"))
                .contentType("application/json")
                .content("{\"playlistName\": \"test\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void removePlaylist_properData_shouldReturn200() throws Exception {
        mvc = standaloneSetup(new PlaylistController(userRepository, playlistService, playlistRepository))
                .build();

        Playlist playlist = new Playlist();
        playlist.setOwner(1L);

        User user = new User();
        user.setId(1L);

        when(playlistRepository.findById(any())).thenReturn(Optional.of(playlist));
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));

        mvc.perform(delete("/playlist/1").principal(new PrincipalImpl("test")))
                .andExpect(status().isOk());
    }

    @Test
    void removePlaylist_principalIsNotAOwnerOfPlaylist_shouldReturn403() throws Exception {
        mvc = standaloneSetup(new PlaylistController(userRepository, playlistService, playlistRepository))
                .build();

        Playlist playlist = new Playlist();
        playlist.setOwner(2L);

        User user = new User();
        user.setId(1L);

        when(playlistRepository.findById(any())).thenReturn(Optional.of(playlist));
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));

        mvc.perform(delete("/playlist/1").principal(new PrincipalImpl("test")))
                .andExpect(status().isForbidden());
    }

    @Test
    void removePlaylist_playlistDoesntExist_shouldReturn404() throws Exception {
        mvc = standaloneSetup(new PlaylistController(userRepository, playlistService, playlistRepository))
                .build();

        User user = new User();
        user.setId(1L);

        when(playlistRepository.findById(any())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));

        mvc.perform(delete("/playlist/1").principal(new PrincipalImpl("test")))
                .andExpect(status().isNotFound());
    }
}