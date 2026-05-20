package com.example.campusanimal.repository;

import com.example.campusanimal.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByUserIdOrderByCreateTimeDesc(Long userId);
}
