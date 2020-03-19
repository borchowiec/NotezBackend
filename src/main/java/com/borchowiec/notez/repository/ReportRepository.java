package com.borchowiec.notez.repository;

import com.borchowiec.notez.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
