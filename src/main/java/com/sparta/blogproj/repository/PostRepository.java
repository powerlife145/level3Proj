package com.sparta.blogproj.repository;

import com.sparta.blogproj.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
        List<Post> findAllByOrderByModifiedAtDesc();

        Optional<Post> findByUser_Id(Long userId);

}
