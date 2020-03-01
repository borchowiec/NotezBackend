package com.borchowiec.notez.repository;

import com.borchowiec.notez.model.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
}
