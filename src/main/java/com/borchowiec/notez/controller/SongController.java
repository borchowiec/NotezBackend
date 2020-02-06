package com.borchowiec.notez.controller;

import com.borchowiec.notez.model.Song;
import com.borchowiec.notez.repository.SongRepository;
import com.borchowiec.notez.service.SongService;
import javassist.NotFoundException;
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

    @ExceptionHandler(NotFoundException.class)
    public void handleNotFound(HttpServletResponse res, NotFoundException e) throws IOException {
        res.sendError(404, e.getMessage());
    }
}