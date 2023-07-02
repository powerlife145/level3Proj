package com.sparta.blogproj.controller;

import com.sparta.blogproj.dto.PasswordDto;
import com.sparta.blogproj.dto.PostRequestDto;
import com.sparta.blogproj.dto.PostResponseDto;
import com.sparta.blogproj.service.PostService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // 전체 게시글 목록 조회 API
    @GetMapping("/posts")
    public List<PostResponseDto> getPosts() {
        return postService.getPosts();
    }

    // 게시글 작성 API
    @PostMapping("/posts")
    public PostResponseDto createPost(@RequestBody PostRequestDto requestDto) {
        return postService.createPost(requestDto);
    }

    // 선택한 게시글 조회 API
    @GetMapping("/posts/{id}")
    public PostResponseDto getPostById(@PathVariable Long id) {
        return postService.getPostById(id);
    }

    // 선택한 게시글 수정 API
    @PutMapping("/posts/{id}")
    public PostResponseDto updatePost(@PathVariable Long id, @RequestBody PostRequestDto requestDto) {
        return postService.updatePost(id, requestDto);
    }

    // 선택한 게시글 삭제 API
    @DeleteMapping("/posts/{id}")
    public String deletePost(@PathVariable Long id, @RequestBody PasswordDto passwordDto) {
        return postService.deletePost(id,passwordDto);
    }

}