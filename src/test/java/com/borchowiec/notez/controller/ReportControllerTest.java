package com.borchowiec.notez.controller;

import com.borchowiec.notez.model.Song;
import com.borchowiec.notez.payload.ReportRequest;
import com.borchowiec.notez.repository.ReportRepository;
import com.borchowiec.notez.repository.RoleRepository;
import com.borchowiec.notez.repository.SongRepository;
import com.borchowiec.notez.repository.UserRepository;
import com.borchowiec.notez.security.CustomUserDetailsService;
import com.borchowiec.notez.security.JwtAuthenticationEntryPoint;
import com.borchowiec.notez.security.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@WebMvcTest(ReportController.class)
class ReportControllerTest {

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
    private SongRepository songRepository;

    @MockBean
    private ReportRepository reportRepository;

    @BeforeEach
    void buildMvc() {
        mvc = standaloneSetup(new ReportController(songRepository, reportRepository)).build();
    }

    @Test
    void addReport_properData_shouldReturn200() throws Exception {
        // given
        Song song = new Song();
        song.setId(12L);
        ReportRequest reportRequest = new ReportRequest("some report content", song.getId());

        // when
        when(songRepository.findById(anyLong())).thenReturn(Optional.of(song));

        // then
        mvc.perform(post("/report")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(reportRequest)))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    void addReport_songDoesntExists_shouldReturn404() throws Exception {
        // given
        ReportRequest reportRequest = new ReportRequest("some report content", 12L);

        // when
        when(songRepository.findById(anyLong())).thenReturn(Optional.empty());

        // then
        mvc.perform(post("/report")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(reportRequest)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}