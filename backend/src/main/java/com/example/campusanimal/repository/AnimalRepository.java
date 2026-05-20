package com.example.campusanimal.repository;

import com.example.campusanimal.entity.Animal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnimalRepository extends JpaRepository<Animal, Long> {

    List<Animal> findByCreateUserIdOrderByIdDesc(Long createUserId);

    List<Animal> findByAuditStatusOrderByIdDesc(String auditStatus);

    Optional<Animal> findFirstByName(String name);
}
