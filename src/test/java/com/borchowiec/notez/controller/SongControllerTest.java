package com.borchowiec.notez.controller;

import com.borchowiec.notez.model.SearchResult;
import com.borchowiec.notez.model.Song;
import com.borchowiec.notez.repository.SongRepository;
import com.borchowiec.notez.service.SongService;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

    @Test
    void getSong_songExists() throws Exception {
        Song song = new Song();
        song.setId(2);
        song.setAlbum("album");
        song.setAuthor("author");
        song.setContent("<span class=\"t9 tone\"></span> some text\nThey're forming");
        song.setName("song");
        Optional<Song> returnedValue = Optional.of(song);

        when(songRepository.findById(any(Long.class))).thenReturn(returnedValue);

        mvc.perform(get("/song/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) song.getId())))
                .andExpect(jsonPath("$.album", is(song.getAlbum())))
                .andExpect(jsonPath("$.content", is(song.getContent())))
                .andExpect(jsonPath("$.name", is(song.getName())));

    }

    @Test
    void getSong_songDoesntExist() throws Exception {
        when(songRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        mvc.perform(get("/song/2"))
                .andExpect(status().is4xxClientError());

    }

    @Test
    void getSongs() throws Exception {
        Song[] songs = {new Song(), new Song()};
        songs[0].setId(2);
        songs[0].setAlbum("album");
        songs[0].setAuthor("author");
        songs[0].setContent("<span class=\"t9 tone\"></span> some text\nThey're forming");
        songs[0].setName("song");

        songs[1].setId(3);
        songs[1].setAlbum("next album");
        songs[1].setAuthor("authorsss");
        songs[1].setContent("");
        songs[1].setName("name");

        when(songRepository.findAll()).thenReturn(Arrays.asList(songs));

        mvc.perform(get("/songs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is((int) songs[0].getId())))
                .andExpect(jsonPath("$[0].album", is(songs[0].getAlbum())))
                .andExpect(jsonPath("$[0].content", is(songs[0].getContent())))
                .andExpect(jsonPath("$[0].name", is(songs[0].getName())))
                .andExpect(jsonPath("$[1].id", is((int) songs[1].getId())))
                .andExpect(jsonPath("$[1].album", is(songs[1].getAlbum())))
                .andExpect(jsonPath("$[1].content", is(songs[1].getContent())))
                .andExpect(jsonPath("$[1].name", is(songs[1].getName())));
    }

    @Test
    void getSongsByPhrase_properData() throws Exception {
        Song[] songs = {new Song(), new Song(), new Song()};
        songs[0].setId(2);
        songs[0].setAlbum("album");
        songs[0].setAuthor("author");
        songs[0].setContent("<span class=\"t9 tone\"></span> some text\nThey're forming");
        songs[0].setName("song phrase");

        songs[1].setId(3);
        songs[1].setAlbum("next album");
        songs[1].setAuthor("phrase");
        songs[1].setContent("");
        songs[1].setName("name");

        songs[2].setId(4);
        songs[2].setAlbum("next album");
        songs[2].setAuthor("authors");
        songs[2].setContent("asdasphrasedasdasd\n\n");
        songs[2].setName("title");

        SearchResult searchResult = new SearchResult();
        searchResult.setByName(Collections.singletonList(songs[0]));
        searchResult.setByAuthor(Collections.singletonList(songs[1]));
        searchResult.setByAlbum(Collections.singletonList(songs[2]));

        when(songRepository.findByNameIgnoreCase(any(), any())).thenReturn(new LinkedList<>());
        when(songRepository.findByNameContainingIgnoreCase(any(), any())).thenReturn(searchResult.getByName());
        when(songRepository.findByAuthorIgnoreCase(any(), any())).thenReturn(searchResult.getByAuthor());
        when(songRepository.findByAuthorContainingIgnoreCase(any(), any())).thenReturn(new LinkedList<>());
        when(songRepository.findByAlbumIgnoreCase(any(), any())).thenReturn(new LinkedList<>());
        when(songRepository.findByAlbumContainingIgnoreCase(any(), any())).thenReturn(searchResult.getByAlbum());

        // todo receives sr has wrong byAlbum
        when(songService.combineTwoListsWithoutDuplicatesAndWithSizeLimit(anyList(), anyList(), anyInt()))
                .thenAnswer((Answer<List<Song>>) invocationOnMock -> {
                    if (((List)invocationOnMock.getArgument(0)).size() > 0) {
                        return invocationOnMock.getArgument(0);
                    }
                    else {
                        return invocationOnMock.getArgument(1);
                    }
                });

        mvc.perform(get("/songs/pHrAsE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.byName", hasSize(1)))
                .andExpect(jsonPath("$.byName[0].id", is((int) songs[0].getId())))
                .andExpect(jsonPath("$.byName[0].album", is(songs[0].getAlbum())))
                .andExpect(jsonPath("$.byName[0].content", is(songs[0].getContent())))
                .andExpect(jsonPath("$.byName[0].name", is(songs[0].getName())))
                .andExpect(jsonPath("$.byAuthor", hasSize(1)))
                .andExpect(jsonPath("$.byAuthor[0].id", is((int) songs[1].getId())))
                .andExpect(jsonPath("$.byAuthor[0].album", is(songs[1].getAlbum())))
                .andExpect(jsonPath("$.byAuthor[0].content", is(songs[1].getContent())))
                .andExpect(jsonPath("$.byAuthor[0].name", is(songs[1].getName())))
                .andExpect(jsonPath("$.byAlbum", hasSize(1)))
                .andExpect(jsonPath("$.byAlbum[0].id", is((int) songs[2].getId())))
                .andExpect(jsonPath("$.byAlbum[0].album", is(songs[2].getAlbum())))
                .andExpect(jsonPath("$.byAlbum[0].content", is(songs[2].getContent())))
                .andExpect(jsonPath("$.byAlbum[0].name", is(songs[2].getName())));
    }

    @Test
    void getSongsByPhrase_repositoriesReturnsDuplicates() throws Exception {
        Song[] songs = {new Song(), new Song(), new Song()};
        songs[0].setId(2);
        songs[0].setAlbum("album");
        songs[0].setAuthor("author");
        songs[0].setContent("<span class=\"t9 tone\"></span> some text\nThey're forming");
        songs[0].setName("song phrase");

        songs[1].setId(3);
        songs[1].setAlbum("next album");
        songs[1].setAuthor("phrase");
        songs[1].setContent("");
        songs[1].setName("name");

        songs[2].setId(4);
        songs[2].setAlbum("next album");
        songs[2].setAuthor("authors");
        songs[2].setContent("asdasphrasedasdasd\n\n");
        songs[2].setName("title");

        SearchResult searchResult = new SearchResult();
        searchResult.setByName(Stream.of(songs[0]).collect(Collectors.toList()));
        searchResult.setByAuthor(Stream.of(songs[1]).collect(Collectors.toList()));
        searchResult.setByAlbum(Stream.of(songs[2]).collect(Collectors.toList()));

        // repositories
        when(songRepository.findByNameIgnoreCase(any(), any())).thenReturn(searchResult.getByName());
        when(songRepository.findByNameContainingIgnoreCase(any(), any())).thenReturn(searchResult.getByName());
        when(songRepository.findByAuthorIgnoreCase(any(), any())).thenReturn(searchResult.getByAuthor());
        when(songRepository.findByAuthorContainingIgnoreCase(any(), any())).thenReturn(searchResult.getByAuthor());
        when(songRepository.findByAlbumIgnoreCase(any(), any())).thenReturn(searchResult.getByAlbum());
        when(songRepository.findByAlbumContainingIgnoreCase(any(), any())).thenReturn(searchResult.getByAlbum());

        // services
        when(songService.combineTwoListsWithoutDuplicatesAndWithSizeLimit(anyList(), anyList(), anyInt()))
            .thenAnswer((Answer<List<Song>>) invocationOnMock -> invocationOnMock.getArgument(0));


        mvc.perform(get("/songs/pHrAsE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.byName", hasSize(1)))
                .andExpect(jsonPath("$.byName[0].id", is((int) songs[0].getId())))
                .andExpect(jsonPath("$.byName[0].album", is(songs[0].getAlbum())))
                .andExpect(jsonPath("$.byName[0].content", is(songs[0].getContent())))
                .andExpect(jsonPath("$.byName[0].name", is(songs[0].getName())))
                .andExpect(jsonPath("$.byAuthor", hasSize(1)))
                .andExpect(jsonPath("$.byAuthor[0].id", is((int) songs[1].getId())))
                .andExpect(jsonPath("$.byAuthor[0].album", is(songs[1].getAlbum())))
                .andExpect(jsonPath("$.byAuthor[0].content", is(songs[1].getContent())))
                .andExpect(jsonPath("$.byAuthor[0].name", is(songs[1].getName())))
                .andExpect(jsonPath("$.byAlbum", hasSize(1)))
                .andExpect(jsonPath("$.byAlbum[0].id", is((int) songs[2].getId())))
                .andExpect(jsonPath("$.byAlbum[0].album", is(songs[2].getAlbum())))
                .andExpect(jsonPath("$.byAlbum[0].content", is(songs[2].getContent())))
                .andExpect(jsonPath("$.byAlbum[0].name", is(songs[2].getName())));
    }
}