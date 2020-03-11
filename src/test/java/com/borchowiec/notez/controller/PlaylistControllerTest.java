package com.borchowiec.notez.controller;

import com.borchowiec.notez.model.Playlist;
import com.borchowiec.notez.model.Song;
import com.borchowiec.notez.model.User;
import com.borchowiec.notez.payload.SongAndPlaylistRequest;
import com.borchowiec.notez.payload.AddSongToPlaylistResponse;
import com.borchowiec.notez.payload.PlaylistResponse;
import com.borchowiec.notez.payload.PlaylistsResponse;
import com.borchowiec.notez.repository.PlaylistRepository;
import com.borchowiec.notez.repository.RoleRepository;
import com.borchowiec.notez.repository.SongRepository;
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
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import sun.security.acl.PrincipalImpl;

import java.security.Principal;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
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

    @MockBean
    private SongRepository songRepository;

    @BeforeEach
    void buildMvc() {
        mvc = standaloneSetup(new PlaylistController(userRepository, playlistService, playlistRepository, songRepository))
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
        mvc = standaloneSetup(new PlaylistController(userRepository, playlistService, playlistRepository, songRepository))
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


    @Test
    void addSongToPlaylist_properData_shouldReturn200AndProperResponse() throws Exception {
        // given
        Principal principal = new PrincipalImpl("principal");
        User user = new User();
        user.setId(10L);

        Song song = new Song();
        song.setId(100L);
        song.setName("Test");
        song.setContent("coontent");
        song.setAuthor("author");
        song.setAlbum("album");

        List<Song> songList = Stream.of(new Song(), new Song()).collect(Collectors.toList());

        Playlist playlist = new Playlist();
        playlist.setId(1L);
        playlist.setOwner(10L);
        playlist.setSongs(new LinkedList<>(songList));

        SongAndPlaylistRequest request = new SongAndPlaylistRequest();
        request.setPlaylistId(playlist.getId());
        request.setSongId(song.getId());
        String requestAsString = objectMapper.writeValueAsString(request);

        // when
        when(playlistRepository.findById(anyLong())).thenReturn(Optional.of(playlist));
        when(songRepository.findById(anyLong())).thenReturn(Optional.of(song));
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));

        ResultActions resultActions = mvc.perform(put("/playlist")
                .principal(principal)
                .content(requestAsString)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        String responseAsString = resultActions.andReturn().getResponse().getContentAsString();
        AddSongToPlaylistResponse response = objectMapper.readValue(responseAsString, AddSongToPlaylistResponse.class);

        // then
        AddSongToPlaylistResponse expected = new AddSongToPlaylistResponse();
        expected.setPlaylistId(playlist.getId());
        expected.setSong(song);
        expected.setSongIndex(songList.size());

        assertEquals(expected, response);
    }

    @Test
    void addSongToPlaylist_playlistDoesntExist_shouldReturn404() throws Exception {
        Principal principal = new PrincipalImpl("principal");

        SongAndPlaylistRequest request = new SongAndPlaylistRequest();
        request.setPlaylistId(1L);
        request.setSongId(2L);
        String requestAsString = objectMapper.writeValueAsString(request);

        when(playlistRepository.findById(anyLong())).thenReturn(Optional.empty());

        mvc.perform(put("/playlist")
                .principal(principal)
                .content(requestAsString)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void addSongToPlaylist_songDoesntExist_shouldReturn404() throws Exception {
        Principal principal = new PrincipalImpl("principal");

        Playlist playlist = new Playlist();

        SongAndPlaylistRequest request = new SongAndPlaylistRequest();
        request.setPlaylistId(1L);
        request.setSongId(2L);
        String requestAsString = objectMapper.writeValueAsString(request);

        when(playlistRepository.findById(anyLong())).thenReturn(Optional.of(playlist));
        when(songRepository.findById(anyLong())).thenReturn(Optional.empty());

        mvc.perform(put("/playlist")
                .principal(principal)
                .content(requestAsString)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void addSongToPlaylist_principalIsNotAOwnerOfPlaylist_shouldReturn403() throws Exception {
        Principal principal = new PrincipalImpl("principal");
        User user = new User();
        user.setId(200L);

        Playlist playlist = new Playlist();
        playlist.setOwner(10L);

        Song song = new Song();

        SongAndPlaylistRequest request = new SongAndPlaylistRequest();
        request.setPlaylistId(1L);
        request.setSongId(2L);
        String requestAsString = objectMapper.writeValueAsString(request);

        when(playlistRepository.findById(anyLong())).thenReturn(Optional.of(playlist));
        when(songRepository.findById(anyLong())).thenReturn(Optional.of(song));
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));

        mvc.perform(put("/playlist")
                .principal(principal)
                .content(requestAsString)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void addSongToPlaylist_playlistAlreadyContainsSong_shouldReturn409() throws Exception {
        Principal principal = new PrincipalImpl("principal");
        User user = new User();
        user.setId(10L);

        Song song = new Song();
        song.setId(100L);
        song.setViews(100);
        song.setName("Test");
        song.setContent("coontent");
        song.setAuthor("author");
        song.setAlbum("album");

        Playlist playlist = new Playlist();
        playlist.setId(1L);
        playlist.setOwner(10L);
        playlist.setSongs(Stream.of(new Song(), song).collect(Collectors.toList()));

        SongAndPlaylistRequest request = new SongAndPlaylistRequest();
        request.setPlaylistId(playlist.getId());
        request.setSongId(song.getId());
        String requestAsString = objectMapper.writeValueAsString(request);

        when(playlistRepository.findById(anyLong())).thenReturn(Optional.of(playlist));
        when(songRepository.findById(anyLong())).thenReturn(Optional.of(song));
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));

        mvc.perform(put("/playlist")
                .principal(principal)
                .content(requestAsString)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    void removeSongFromPlaylist_playlistDoesntExist_shouldReturn404() throws Exception {
        SongAndPlaylistRequest request = new SongAndPlaylistRequest();
        request.setPlaylistId(123L);
        request.setSongId(1L);
        String requestAsString = objectMapper.writeValueAsString(request);

        when(playlistRepository.findById(anyLong())).thenReturn(Optional.empty());

        mvc.perform(delete("/playlist")
                .content(requestAsString)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void removeSongFromPlaylist_playlistDoesntContainSong_shouldReturn404() throws Exception {
        Principal principal = new PrincipalImpl("principal");
        User user = new User();
        user.setId(10L);

        Song song = new Song();
        song.setId(100L);
        song.setViews(100);
        song.setName("Test");
        song.setContent("coontent");
        song.setAuthor("author");
        song.setAlbum("album");

        Playlist playlist = new Playlist();
        playlist.setId(1L);
        playlist.setOwner(10L);
        playlist.setSongs(Stream.of(song).collect(Collectors.toList()));

        SongAndPlaylistRequest request = new SongAndPlaylistRequest();
        request.setPlaylistId(playlist.getId());
        request.setSongId(1L);
        String requestAsString = objectMapper.writeValueAsString(request);

        when(playlistRepository.findById(anyLong())).thenReturn(Optional.of(playlist));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        mvc.perform(delete("/playlist")
                .principal(principal)
                .content(requestAsString)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void removeSongFromPlaylist_principalIsNotOwnerOfPlaylist_shouldReturn403() throws Exception {
        Principal principal = new PrincipalImpl("principal");
        User user = new User();
        user.setId(10L);

        Song song = new Song();
        song.setId(100L);
        song.setViews(100);
        song.setName("Test");
        song.setContent("coontent");
        song.setAuthor("author");
        song.setAlbum("album");

        Playlist playlist = new Playlist();
        playlist.setId(1L);
        playlist.setOwner(11L);
        playlist.setSongs(Stream.of(song).collect(Collectors.toList()));

        SongAndPlaylistRequest request = new SongAndPlaylistRequest();
        request.setPlaylistId(playlist.getId());
        request.setSongId(song.getId());
        String requestAsString = objectMapper.writeValueAsString(request);

        when(playlistRepository.findById(anyLong())).thenReturn(Optional.of(playlist));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        mvc.perform(delete("/playlist")
                .principal(principal)
                .content(requestAsString)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void removeSongFromPlaylist_properData_shouldReturn200() throws Exception {
        Principal principal = new PrincipalImpl("principal");
        User user = new User();
        user.setId(10L);

        Song song = new Song();
        song.setId(100L);
        song.setViews(100);
        song.setName("Test");
        song.setContent("coontent");
        song.setAuthor("author");
        song.setAlbum("album");

        Playlist playlist = new Playlist();
        playlist.setId(1L);
        playlist.setOwner(user.getId());
        playlist.setSongs(Stream.of(song).collect(Collectors.toList()));

        SongAndPlaylistRequest request = new SongAndPlaylistRequest();
        request.setPlaylistId(playlist.getId());
        request.setSongId(song.getId());
        String requestAsString = objectMapper.writeValueAsString(request);

        when(playlistRepository.findById(anyLong())).thenReturn(Optional.of(playlist));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        mvc.perform(delete("/playlist")
                .principal(principal)
                .content(requestAsString)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
}