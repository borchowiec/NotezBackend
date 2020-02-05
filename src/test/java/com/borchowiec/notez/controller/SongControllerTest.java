package com.borchowiec.notez.controller;

import com.borchowiec.notez.model.Song;
import com.borchowiec.notez.repository.SongRepository;
import com.borchowiec.notez.service.SongService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static sun.plugin2.util.PojoUtil.toJson;

@WebMvcTest
class SongControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private SongRepository songRepository;

    @MockBean
    private SongService songService;

    @Test
    void addSong() throws Exception {
        Song song = new Song();
        song.setAlbum("album");
        song.setAuthor("author");
        song.setContent("$ch$A5 some text\n$ly$They're forming");
        song.setContent(song.getContent().replaceAll("\n", "\\\\n"));
        song.setName("song");

        when(songService.textToHtml(any(String.class))).thenReturn("<span class=\"t9 tone\"></span> some text\nThey're forming");

        mvc.perform(post("/song")
                .characterEncoding("UTF8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(song)))
                .andExpect(status().isOk());
    }
}