package com.borchowiec.notez.repository;

import com.borchowiec.notez.NotezApplication;
import com.borchowiec.notez.model.Song;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(classes = NotezApplication.class)
class SongRepositoryTest {

    @Autowired
    private SongRepository songRepository;

    private List<Song> dummySongs;

    @BeforeEach
    void putDataToDatabase() {
        dummySongs = new LinkedList<>();

        Song song = new Song();
        // 0
        song.setName("heart");
        song.setAuthor("Bung");
        song.setAlbum("Bare to the bone");
        song.setViews(14);
        dummySongs.add(song);

        // 1
        song = new Song();
        song.setName("Apology Of Anything");
        song.setAuthor("4 Ung");
        song.setAlbum("No discounts");
        song.setViews(1200);
        dummySongs.add(song);

        // 2
        song = new Song();
        song.setName("Sweet Child For An Angel");
        song.setAuthor("Landscaping Appearance 89");
        song.setAlbum("Silver linings");
        song.setViews(3232);
        dummySongs.add(song);

        // 3
        song = new Song();
        song.setName("Forget My Music");
        song.setAuthor("Of Lemon");
        song.setAlbum("Beggars and thieves");
        song.setViews(1);
        dummySongs.add(song);

        // 4
        song = new Song();
        song.setName("Reach For Your Affection");
        song.setAuthor("Significant 41");
        song.setAlbum("No choice");
        song.setViews(1002);
        dummySongs.add(song);

        // 5
        song = new Song();
        song.setName("Sure Romance");
        song.setAuthor("Sung Computer 93");
        song.setAlbum("Atmos");
        song.setViews(233);
        dummySongs.add(song);

        // 6
        song = new Song();
        song.setName("Last Heart");
        song.setAuthor("Some ung");
        song.setAlbum("No ambition");
        song.setViews(98);
        dummySongs.add(song);

        songRepository.saveAll(dummySongs);
    }

    @Test
    void findByNameContainingIgnoreCase() {
        // given
        List<Song> expected = new LinkedList<>();
        expected.add(dummySongs.get(6));
        expected.add(dummySongs.get(0));
        String phrase = "hEaRt";

        // when
        List<Song> received = songRepository.findByNameContainingIgnoreCase(phrase,
                PageRequest.of(0, 3, Sort.by("views").descending()));

        // then
        assertEquals(expected, received);
    }

    @Test
    void findByAuthorContainingIgnoreCase() {
        // given
        List<Song> expected = new LinkedList<>();
        expected.add(dummySongs.get(1));
        expected.add(dummySongs.get(5));
        expected.add(dummySongs.get(6));

        String phrase = "UnG";

        // when
        List<Song> received = songRepository.findByAuthorContainingIgnoreCase(phrase,
                PageRequest.of(0, 3, Sort.by("views").descending()));

        // then
        assertEquals(expected, received);
    }

    @Test
    void findByAlbumContainingIgnoreCase() {
        // given
        List<Song> expected = new LinkedList<>();
        expected.add(dummySongs.get(1));
        expected.add(dummySongs.get(4));
        expected.add(dummySongs.get(6));

        String phrase = "nO";

        // when
        List<Song> received = songRepository.findByAlbumContainingIgnoreCase(phrase,
                PageRequest.of(0, 3, Sort.by("views").descending()));

        // then
        assertEquals(expected, received);
    }
}