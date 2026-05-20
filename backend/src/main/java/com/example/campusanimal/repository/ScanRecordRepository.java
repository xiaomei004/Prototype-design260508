package com.example.campusanimal.repository;

import com.example.campusanimal.entity.ScanRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScanRecordRepository extends JpaRepository<ScanRecord, Long> {

    List<ScanRecord> findByUserIdOrderByCreateTimeDesc(Long userId);
}
