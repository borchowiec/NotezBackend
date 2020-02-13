package com.borchowiec.notez.service;

import com.borchowiec.notez.model.Song;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SongServiceTest {

    @Test
    void textToHtml_normalText() {
        // given
        SongService songService = new SongService();
        String text = "$ch$A5          some text ?!>@?#!@     D5  E5\n" +
                "$ly$They're forming in a straight line.\n" +
                "$ch$A5                                 D5  E5\n" +
                "$ly$They're going through a tight wind.";

        // when
        text = songService.textToHtml(text);

        // then
        String expected = "<span class=\"t9 tone\"></span>5          some text ?!>@?#!@     <span class=\"t2 tone\"></span>5  <span class=\"t4 tone\"></span>5\n" +
                "They're forming in a straight line.\n" +
                "<span class=\"t9 tone\"></span>5                                 <span class=\"t2 tone\"></span>5  <span class=\"t4 tone\"></span>5\n" +
                "They're going through a tight wind.";
        assertEquals(expected, text);
    }

    @Test
    void textToHtml_allTones() {
        // given
        SongService songService = new SongService();
        String text = "$ch$C\n" +
                "$ch$C#\n" +
                "$ch$Db\n" +
                "$ch$D\n" +
                "$ch$D#\n" +
                "$ch$Eb\n" +
                "$ch$E\n" +
                "$ch$F\n" +
                "$ch$F#\n" +
                "$ch$Gb\n" +
                "$ch$G\n" +
                "$ch$G#\n" +
                "$ch$Ab\n" +
                "$ch$A\n" +
                "$ch$A#\n" +
                "$ch$Bb\n" +
                "$ch$B";

        // when
        text = songService.textToHtml(text);

        // then
        String expected = "<span class=\"t0 tone\"></span>\n" +
                "<span class=\"t1 tone\"></span>\n" +
                "<span class=\"t1 tone\"></span>\n" +
                "<span class=\"t2 tone\"></span>\n" +
                "<span class=\"t3 tone\"></span>\n" +
                "<span class=\"t3 tone\"></span>\n" +
                "<span class=\"t4 tone\"></span>\n" +
                "<span class=\"t5 tone\"></span>\n" +
                "<span class=\"t6 tone\"></span>\n" +
                "<span class=\"t6 tone\"></span>\n" +
                "<span class=\"t7 tone\"></span>\n" +
                "<span class=\"t8 tone\"></span>\n" +
                "<span class=\"t8 tone\"></span>\n" +
                "<span class=\"t9 tone\"></span>\n" +
                "<span class=\"t10 tone\"></span>\n" +
                "<span class=\"t10 tone\"></span>\n" +
                "<span class=\"t11 tone\"></span>";
        assertEquals(expected, text);
    }

    @Test
    void textToHtml_emptyLines() {
        // given
        SongService songService = new SongService();
        String text = "$ch$A5          some text ?!>@?#!@     D5  E5\n" +
                "They're forming in $ly$a straight line.\n" +
                "A5                                 D5  E5\n" +
                "$ly$They're going through a tight wind.";

        // when
        text = songService.textToHtml(text);

        // then
        String expected = "<span class=\"t9 tone\"></span>5          some text ?!>@?#!@     <span class=\"t2 tone\"></span>5  <span class=\"t4 tone\"></span>5\n" +
                "They're going through a tight wind.";
        assertEquals(expected, text);
    }

    @Test
    void combineTwoListsWithoutDuplicatesAndWithSizeLimit_noDuplicatesAboveLimit() {
        // given
        Song[] songs = new Song[]{
            new Song(0, "name", "author", "album", "content", 10),
            new Song(1, "name", "author", "album", "content", 10),
            new Song(2, "name", "author", "album", "content", 10),
            new Song(3, "name", "author", "album", "content", 10),
            new Song(4, "name", "author", "album", "content", 10),
            new Song(5, "name", "author", "album", "content", 10)
        };
        List<Song> first = Stream.of(songs[0], songs[1], songs[2]).collect(Collectors.toList());
        List<Song> second = Stream.of(songs[3], songs[4], songs[5]).collect(Collectors.toList());
        List<Song> expected = Stream.of(songs).collect(Collectors.toList());

        // when
        SongService songService = new SongService();
        List<Song> result = songService.combineTwoListsWithoutDuplicatesAndWithSizeLimit(first, second, 6);

        // then
        assertEquals(expected, result);
    }

    @Test
    void combineTwoListsWithoutDuplicatesAndWithSizeLimit_noDuplicatesBelowLimit() {
// given
        Song[] songs = new Song[]{
                new Song(0, "name", "author", "album", "content", 10),
                new Song(1, "name", "author", "album", "content", 10),
                new Song(2, "name", "author", "album", "content", 10),
                new Song(3, "name", "author", "album", "content", 10),
                new Song(4, "name", "author", "album", "content", 10),
                new Song(5, "name", "author", "album", "content", 10)
        };
        List<Song> first = Stream.of(songs[0], songs[1], songs[2]).collect(Collectors.toList());
        List<Song> second = Stream.of(songs[3], songs[4], songs[5]).collect(Collectors.toList());
        List<Song> expected = Stream
                .of(Arrays.copyOfRange(songs, 0, 4))
                .collect(Collectors.toList());

        // when
        SongService songService = new SongService();
        List<Song> result = songService.combineTwoListsWithoutDuplicatesAndWithSizeLimit(first, second, 4);

        // then
        assertEquals(expected, result);
    }

    @Test
    void combineTwoListsWithoutDuplicatesAndWithSizeLimit_duplicates() {
        // given
        Song[] songs = new Song[]{
                new Song(0, "name", "author", "album", "content", 10),
                new Song(1, "name", "author", "album", "content", 10),
                new Song(2, "name", "author", "album", "content", 10),
                new Song(3, "name", "author", "album", "content", 10),
                new Song(4, "name", "author", "album", "content", 10),
                new Song(5, "name", "author", "album", "content", 10)
        };
        List<Song> first = Stream.of(songs[0], songs[1], songs[3]).collect(Collectors.toList());
        List<Song> second = Stream.of(songs[1], songs[3], songs[5]).collect(Collectors.toList());
        List<Song> expected = Stream.of(songs[0], songs[1], songs[3], songs[5]).collect(Collectors.toList());

        // when
        SongService songService = new SongService();
        List<Song> result = songService.combineTwoListsWithoutDuplicatesAndWithSizeLimit(first, second, 6);

        // then
        assertEquals(expected, result);
    }

    @Test
    void combineTwoListsWithoutDuplicatesAndWithSizeLimit_firstListIsToBig() {
        // given
        Song[] songs = new Song[]{
                new Song(0, "name", "author", "album", "content", 10),
                new Song(1, "name", "author", "album", "content", 10),
                new Song(2, "name", "author", "album", "content", 10),
                new Song(3, "name", "author", "album", "content", 10),
                new Song(4, "name", "author", "album", "content", 10),
                new Song(5, "name", "author", "album", "content", 10)
        };
        List<Song> first = Stream.of(songs[0], songs[1], songs[2], songs[3]).collect(Collectors.toList());
        List<Song> second = Stream.of(songs[1], songs[3], songs[5]).collect(Collectors.toList());
        List<Song> expected = Stream.of(songs[0], songs[1], songs[2]).collect(Collectors.toList());

        // when
        SongService songService = new SongService();
        List<Song> result = songService.combineTwoListsWithoutDuplicatesAndWithSizeLimit(first, second, 3);

        // then
        assertEquals(expected, result);
    }
}