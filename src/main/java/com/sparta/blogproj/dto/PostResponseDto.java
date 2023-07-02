package com.sparta.blogproj.dto;

import com.sparta.blogproj.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostResponseDto {
    private Long id;
    private String title;
    private String username;
    private String contents;
    private LocalDateTime createAt;
    private LocalDateTime modifiedAt;

    public PostResponseDto(Post post) {
        this.createAt = post.getCreatedAt();
        this.modifiedAt = post.getModifiedAt();
        this.id = post.getId();
        this.title = post.getTitle();
        this.contents = post.getContents();
        this.username = post.getUsername();
    }
}
