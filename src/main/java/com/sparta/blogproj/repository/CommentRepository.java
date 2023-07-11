package com.sparta.blogproj.repository;

import com.sparta.blogproj.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
