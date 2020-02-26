package com.borchowiec.notez.repository;

import com.borchowiec.notez.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
