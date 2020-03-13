package com.borchowiec.notez.service;

import com.borchowiec.notez.model.Playlist;
import com.borchowiec.notez.repository.PlaylistRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class PlaylistServiceTest {

    @MockBean
    private PlaylistRepository playlistRepository;

    @Test
    void createPlaylist_properData_shouldReturnNewPlaylist() {
        // given
        String playlistName = "playlist";
        long ownerId = 123L;

        // when
        PlaylistService playlistService = new PlaylistService(playlistRepository);
        Playlist result = playlistService.createPlaylist(playlistName, ownerId);

        // then
        assertEquals(playlistName, result.getName());
        assertEquals(ownerId, result.getOwner());
        assertEquals(0, result.getSongs().size());
    }
}