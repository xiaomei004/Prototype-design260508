package com.example.campusanimal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "animals")
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 20)
    private String species;

    @Column(length = 20)
    private String gender;

    @Column(length = 50)
    private String color;

    @Column(nullable = false, length = 100)
    private String location;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(length = 255)
    private String features;

    @Column(length = 255)
    private String imageUrl;

    @Column(nullable = false)
    private Integer sterilized;

    @Column(length = 50)
    private String vaccineStatus;

    private LocalDateTime firstFoundTime;

    @Column(nullable = false, length = 20)
    private String auditStatus;

    @Column(nullable = false)
    private Long createUserId;

    @Column(nullable = false)
    private LocalDateTime createTime;
}
