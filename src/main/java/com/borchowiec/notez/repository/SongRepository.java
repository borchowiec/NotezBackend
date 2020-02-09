package com.borchowiec.notez.repository;

import com.borchowiec.notez.model.Song;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SongRepository extends JpaRepository<Song, Long> {
    List<Song> findByNameContainingIgnoreCase(String phrase, Pageable pageable);
    List<Song> findByAuthorContainingIgnoreCase(String phrase, Pageable pageable);
    List<Song> findByAlbumContainingIgnoreCase(String phrase, Pageable pageable);
}
