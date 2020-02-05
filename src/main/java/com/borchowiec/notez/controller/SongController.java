package com.borchowiec.notez.controller;

import com.borchowiec.notez.model.Song;
import com.borchowiec.notez.repository.SongRepository;
import com.borchowiec.notez.service.SongService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
}
