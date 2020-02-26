package com.borchowiec.notez.repository;

import com.borchowiec.notez.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
