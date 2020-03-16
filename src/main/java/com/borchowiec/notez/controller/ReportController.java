package com.borchowiec.notez.controller;

import com.borchowiec.notez.exception.SongNotFoundException;
import com.borchowiec.notez.model.Report;
import com.borchowiec.notez.model.Song;
import com.borchowiec.notez.payload.ReportRequest;
import com.borchowiec.notez.repository.ReportRepository;
import com.borchowiec.notez.repository.SongRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReportController {

    private final SongRepository songRepository;
    private final ReportRepository reportRepository;

    public ReportController(SongRepository songRepository, ReportRepository reportRepository) {
        this.songRepository = songRepository;
        this.reportRepository = reportRepository;
    }

    @PostMapping("/report")
    public void addReport(@RequestBody ReportRequest request) {
        long songId = request.getSongId();
        Song song = songRepository.findById(songId).orElseThrow(() -> new SongNotFoundException(songId));

        Report report = new Report();
        report.setSong(song);
        report.setValue(request.getContent());

        reportRepository.save(report);
    }
}
