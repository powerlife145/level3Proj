package com.sparta.blogproj.controller;

import com.sparta.blogproj.dto.*;
import com.sparta.blogproj.jwt.JwtUtil;
import com.sparta.blogproj.service.PostService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    public static final String AUTHORIZATION_HEADER = "Authorization";

    // 회원가입 API
    @PostMapping("/post/auth/signup")
    public ResponseEntity<StatusMessageDto> createUser(@RequestBody @Valid UserInformationDto requestDto){
        return postService.createUser(requestDto);
    }

    // 로그인 API
    @PostMapping("/auth/login")
    public ResponseEntity<StatusMessageDto> login(@RequestBody UserInformationDto requestDto, HttpServletResponse res){
        return postService.login(requestDto, res);
    }

    // 전체 게시글 목록 조회 API
    @GetMapping("/posts")

    public List<PostResponseDto> getPosts() {
        return postService.getPosts();
    }

    // 게시글 작성 API
    @PostMapping("/post")
    public PostResponseDto createPost(@RequestBody PostRequestDto requestDto, HttpServletRequest req) {
        return postService.createPost(requestDto,req);
    }

    // 선택한 게시글 조회 API
    @GetMapping("/post/{id}")
    public PostResponseDto getPostById(@PathVariable Long id) {
        return postService.getPostById(id);
    }

    // 선택한 게시글 수정 API
    @PutMapping("/post/{id}")
    public PostResponseDto updatePost(@PathVariable Long id, @RequestBody PostRequestDto requestDto) {
        return postService.updatePost(id, requestDto);
    }

    // 선택한 게시글 삭제 API
    @DeleteMapping("/post/{id}")
    public SuccessDto deletePost(@PathVariable Long id, @RequestBody PasswordDto passwordDto) {
        return postService.deletePost(id, passwordDto);
    }

    public static void addCookie(String cookieValue, HttpServletResponse res) {
        try {
            cookieValue = URLEncoder.encode(cookieValue, "utf-8").replaceAll("\\+", "%20"); // Cookie Value 에는 공백이 불가능해서 encoding 진행

            Cookie cookie = new Cookie(AUTHORIZATION_HEADER, cookieValue); // Name-Value
            cookie.setPath("/");
            cookie.setMaxAge(30 * 60);

            // Response 객체에 Cookie 추가
            res.addCookie(cookie);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
