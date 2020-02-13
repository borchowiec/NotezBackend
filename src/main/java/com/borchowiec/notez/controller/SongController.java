package com.borchowiec.notez.controller;

import com.borchowiec.notez.model.SearchResult;
import com.borchowiec.notez.model.Song;
import com.borchowiec.notez.repository.SongRepository;
import com.borchowiec.notez.service.SongService;
import javassist.NotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
public class SongController {

    private SongRepository songRepository;
    private SongService songService;

    public SongController(SongRepository songRepository, SongService songService) {
        this.songRepository = songRepository;
        this.songService = songService;
    }

    /**
     * Converts lyrics to proper html text and then, puts song to database.
     * @param song Song that will be added to database.
     */
    @CrossOrigin("*") //todo temporary
    @PostMapping("/song")
    public void addSong(@RequestBody Song song) {

        String content = song.getContent();
        content = content.replaceAll("\\\\n","\n");
        content = songService.textToHtml(content);
        song.setContent(content);

        songRepository.save(song);
    }

    /**
     * Finds and returns song of specific id.
     * @param id Id of a song.
     * @return Song of specific id.
     * @throws NotFoundException When there is no song of given id.
     */
    @GetMapping("/song/{id}")
    public Song getSong(@PathVariable Long id) throws NotFoundException {
        Optional<Song> result = songRepository.findById(id);

        if (result.isPresent()) {
            return result.get();
        }
        else {
            throw new NotFoundException("Not found song of id: " + id);
        }
    }

    /**
     * @return List of all songs.
     */
    @GetMapping("/songs")
    public List<Song> getSongs() {
        return songRepository.findAll();
    }

    /**
     * Looks for songs that contains phrase in name of song, author of song or album of song.
     * @param phrase Given phrase.
     * @return Songs that contains phrase in name of song, author of song or album of song.
     */
    @GetMapping("/songs/{phrase}")
    public SearchResult getSongsByPhrase(@PathVariable String phrase) {
        SearchResult searchResult = new SearchResult();
        Pageable pageable = PageRequest.of(0, 3, Sort.by("views").descending());

        List<Song> byName = songRepository.findByNameIgnoreCase(phrase, pageable);
        if (byName.size() < 3) {
            List<Song> byNameContainingPhrase = songRepository.findByNameContainingIgnoreCase(phrase, pageable);
            byName = songService.combineTwoListsWithoutDuplicatesAndWithSizeLimit(byName, byNameContainingPhrase, 3);
        }
        searchResult.setByName(byName);

        List<Song> byAuthor = songRepository.findByAuthorIgnoreCase(phrase, pageable);
        if (byAuthor.size() < 3) {
            List<Song> byAuthorContainingPhrase = songRepository.findByAuthorContainingIgnoreCase(phrase, pageable);
            byAuthor = songService.combineTwoListsWithoutDuplicatesAndWithSizeLimit(byAuthor, byAuthorContainingPhrase, 3);
        }
        searchResult.setByAuthor(byAuthor);

        List<Song> byAlbum = songRepository.findByAlbumIgnoreCase(phrase, pageable);
        if (byAlbum.size() < 3) {
            List<Song> byAlbumContainingPhrase = songRepository.findByAlbumContainingIgnoreCase(phrase, pageable);
            byAlbum = songService.combineTwoListsWithoutDuplicatesAndWithSizeLimit(byAlbum, byAlbumContainingPhrase, 3);
        }
        searchResult.setByAlbum(byAlbum);

        return searchResult;
    }


    @ExceptionHandler(NotFoundException.class)
    public void handleNotFound(HttpServletResponse res, NotFoundException e) throws IOException {
        res.sendError(404, e.getMessage());
    }
}