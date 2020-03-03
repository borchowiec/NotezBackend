package com.borchowiec.notez.controller;

import com.borchowiec.notez.model.Playlist;
import com.borchowiec.notez.model.Song;
import com.borchowiec.notez.model.User;
import com.borchowiec.notez.payload.PlaylistResponse;
import com.borchowiec.notez.payload.PlaylistsResponse;
import com.borchowiec.notez.repository.PlaylistRepository;
import com.borchowiec.notez.repository.RoleRepository;
import com.borchowiec.notez.repository.UserRepository;
import com.borchowiec.notez.security.CustomUserDetailsService;
import com.borchowiec.notez.security.JwtAuthenticationEntryPoint;
import com.borchowiec.notez.security.JwtTokenProvider;
import com.borchowiec.notez.service.PlaylistService;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@WebMvcTest(PlaylistController.class)
class PlaylistControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

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

    @BeforeEach
    void buildMvc() {
        mvc = standaloneSetup(new PlaylistController(userRepository, playlistService, playlistRepository))
                .build();
    }

    @Test
    void createPlaylist() throws Exception {
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(new User()));

        mvc.perform(post("/playlist")
                .principal(new PrincipalImpl("principal"))
                .contentType("application/json")
                .content("{\"playlistName\": \"test\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void removePlaylist_properData_shouldReturn200() throws Exception {
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
        User user = new User();
        user.setId(1L);

        when(playlistRepository.findById(any())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));

        mvc.perform(delete("/playlist/1").principal(new PrincipalImpl("test")))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPlaylist_playlistDoesntExist_shouldReturn404() throws Exception {
        when(playlistRepository.findById(any())).thenReturn(Optional.empty());

        mvc.perform(get("/playlist/1"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void getPlaylist_principalIsOwnerOfPlaylist_shouldReturnPlaylistWithIsOwnerOnTrue() throws Exception {
        // given
        User user = new User();
        user.setId(1L);

        Playlist playlist = new Playlist();
        playlist.setOwner(1L);
        playlist.setSongs(Stream.of(
                new Song(2L, "title", "authorrr", "album", "conteeent", 0),
                new Song(3L, "tle", "autr", "album", "asd sd sdds", 0),
                new Song(4L, "ti", "authorrr", "m", "conteeent", 0)
        ).collect(Collectors.toList()));
        playlist.setName("playlist");

        PlaylistResponse expected = new PlaylistResponse();
        expected.setOwner(true);
        expected.setPlaylist(playlist);

        // when
        when(playlistRepository.findById(any())).thenReturn(Optional.of(playlist));
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
        ResultActions resultActions = mvc.perform(get("/playlist/1").principal(new PrincipalImpl("pri")))
                .andDo(print())
                .andExpect(status().isOk());

        String responseAsString = resultActions.andReturn().getResponse().getContentAsString();
        PlaylistResponse response = objectMapper.readValue(responseAsString, PlaylistResponse.class);

        // then
        assertEquals(expected, response);
    }

    @Test
    void getPlaylist_principalIsNotOwnerOfPlaylist_shouldReturnPlaylistWithIsOwnerOnFalse() throws Exception {
        // given
        User user = new User();
        user.setId(10L);

        Playlist playlist = new Playlist();
        playlist.setOwner(1L);
        playlist.setSongs(Stream.of(
                new Song(2L, "title", "authorrr", "album", "conteeent", 0),
                new Song(3L, "tle", "autr", "album", "asd sd sdds", 0),
                new Song(4L, "ti", "authorrr", "m", "conteeent", 0)
        ).collect(Collectors.toList()));
        playlist.setName("playlist");

        PlaylistResponse expected = new PlaylistResponse();
        expected.setOwner(false);
        expected.setPlaylist(playlist);

        // when
        when(playlistRepository.findById(any())).thenReturn(Optional.of(playlist));
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
        ResultActions resultActions = mvc.perform(get("/playlist/1").principal(new PrincipalImpl("pri")))
                .andDo(print())
                .andExpect(status().isOk());

        String responseAsString = resultActions.andReturn().getResponse().getContentAsString();
        PlaylistResponse response = objectMapper.readValue(responseAsString, PlaylistResponse.class);

        // then
        assertEquals(expected, response);
    }

    @Test
    void getPlaylist_noLoggedInUser_shouldReturnPlaylistWithIsOwnerOnFalse() throws Exception {
        // given
        Playlist playlist = new Playlist();
        playlist.setOwner(1L);
        playlist.setSongs(Stream.of(
                new Song(2L, "title", "authorrr", "album", "conteeent", 0),
                new Song(3L, "tle", "autr", "album", "asd sd sdds", 0),
                new Song(4L, "ti", "authorrr", "m", "conteeent", 0)
        ).collect(Collectors.toList()));
        playlist.setName("playlist");

        PlaylistResponse expected = new PlaylistResponse();
        expected.setOwner(false);
        expected.setPlaylist(playlist);

        // when
        when(playlistRepository.findById(any())).thenReturn(Optional.of(playlist));
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
        ResultActions resultActions = mvc.perform(get("/playlist/1"))
                .andDo(print())
                .andExpect(status().isOk());

        String responseAsString = resultActions.andReturn().getResponse().getContentAsString();
        PlaylistResponse response = objectMapper.readValue(responseAsString, PlaylistResponse.class);

        // then
        assertEquals(expected, response);
    }


    @Test
    void getUsersPlaylists_userDoesntExist_shouldReturn404() throws Exception {
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        mvc.perform(get("/playlists/1"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void getUsersPlaylists_principalIsOwnerOfPlaylists_shouldReturnPlaylistsWithIsOwnerOnTrue() throws Exception {
        // given
        User user = new User();
        user.setUsername("usrname");
        user.setId(1L);

        Principal principal = new PrincipalImpl("usrname");

        Playlist playlist = new Playlist();
        playlist.setOwner(1L);
        playlist.setSongs(Stream.of(
                new Song(2L, "title", "authorrr", "album", "conteeent", 0),
                new Song(3L, "tle", "autr", "album", "asd sd sdds", 0),
                new Song(4L, "ti", "authorrr", "m", "conteeent", 0)
        ).collect(Collectors.toList()));
        playlist.setName("playlist");

        List<Playlist> playlists = Stream.of(playlist, playlist, playlist, playlist).collect(Collectors.toList());

        PlaylistsResponse expected = new PlaylistsResponse();
        expected.setPlaylists(playlists);
        expected.setOwner(true);

        // when
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(playlistRepository.findByOwner(anyLong())).thenReturn(playlists);

        ResultActions resultActions = mvc.perform(get("/playlists/1").principal(principal))
                .andDo(print())
                .andExpect(status().isOk());

        String responseAsString = resultActions.andReturn().getResponse().getContentAsString();
        PlaylistsResponse response = objectMapper.readValue(responseAsString, PlaylistsResponse.class);

        // then
        assertEquals(expected, response);
    }

    @Test
    void getUsersPlaylists_principalIsNotOwnerOfPlaylists_shouldReturnPlaylistsWithIsOwnerOnFalse() throws Exception {
        // given
        User user = new User();
        user.setUsername("other");
        user.setId(2L);

        Principal principal = new PrincipalImpl("usrname");

        Playlist playlist = new Playlist();
        playlist.setOwner(2L);
        playlist.setSongs(Stream.of(
                new Song(2L, "title", "authorrr", "album", "conteeent", 0),
                new Song(3L, "tle", "autr", "album", "asd sd sdds", 0),
                new Song(4L, "ti", "authorrr", "m", "conteeent", 0)
        ).collect(Collectors.toList()));
        playlist.setName("playlist");

        List<Playlist> playlists = Stream.of(playlist, playlist, playlist, playlist).collect(Collectors.toList());

        PlaylistsResponse expected = new PlaylistsResponse();
        expected.setPlaylists(playlists);
        expected.setOwner(false);

        // when
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(playlistRepository.findByOwner(anyLong())).thenReturn(playlists);

        ResultActions resultActions = mvc.perform(get("/playlists/1").principal(principal))
                .andDo(print())
                .andExpect(status().isOk());

        String responseAsString = resultActions.andReturn().getResponse().getContentAsString();
        PlaylistsResponse response = objectMapper.readValue(responseAsString, PlaylistsResponse.class);

        // then
        assertEquals(expected, response);
    }

    @Test
    void getUsersPlaylists_noLoggedInUser_shouldReturnPlaylistsWithIsOwnerOnFalse() throws Exception {
        // given
        User user = new User();
        user.setUsername("other");
        user.setId(2L);

        Playlist playlist = new Playlist();
        playlist.setOwner(2L);
        playlist.setSongs(Stream.of(
                new Song(2L, "title", "authorrr", "album", "conteeent", 0),
                new Song(3L, "tle", "autr", "album", "asd sd sdds", 0),
                new Song(4L, "ti", "authorrr", "m", "conteeent", 0)
        ).collect(Collectors.toList()));
        playlist.setName("playlist");

        List<Playlist> playlists = Stream.of(playlist, playlist, playlist, playlist).collect(Collectors.toList());

        PlaylistsResponse expected = new PlaylistsResponse();
        expected.setPlaylists(playlists);
        expected.setOwner(false);

        // when
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(playlistRepository.findByOwner(anyLong())).thenReturn(playlists);

        ResultActions resultActions = mvc.perform(get("/playlists/1"))
                .andDo(print())
                .andExpect(status().isOk());

        String responseAsString = resultActions.andReturn().getResponse().getContentAsString();
        PlaylistsResponse response = objectMapper.readValue(responseAsString, PlaylistsResponse.class);

        // then
        assertEquals(expected, response);
    }
}