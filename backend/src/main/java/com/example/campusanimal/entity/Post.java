package com.example.campusanimal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    private Long animalId;

    @Column(nullable = false, length = 20)
    private String type;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(length = 255)
    private String imageUrl;

    @Column(length = 100)
    private String location;

    @Column(nullable = false)
    private LocalDateTime createTime;
}
