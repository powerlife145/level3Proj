package com.sparta.blogproj.service;

import com.sparta.blogproj.dto.PostListResponseDto;
import com.sparta.blogproj.dto.PostRequestDto;
import com.sparta.blogproj.dto.PostResponseDto;
import com.sparta.blogproj.dto.StatusMessageDto;
import com.sparta.blogproj.entity.Post;
import com.sparta.blogproj.entity.User;
import com.sparta.blogproj.jwt.JwtUtil;
import com.sparta.blogproj.repository.PostRepository;
import com.sparta.blogproj.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;


@Service
public class PostService {

    private final PostRepository postRepository;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository, JwtUtil jwtUtil, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    // 게시글 생성
    public PostResponseDto createPost(PostRequestDto requestDto, HttpServletRequest req) {
        User user = findUser(req);

        Post post = new Post(requestDto, user);
        Post savePost = postRepository.save(post);
        PostResponseDto postResponseDto = new PostResponseDto(savePost);
        return postResponseDto;
    }

    // 전체 게시글 조회
    public PostListResponseDto getPosts() {
        List<PostResponseDto> postResponseDtoList = postRepository.findAllByOrderByModifiedAtDesc()
                .stream().map((PostResponseDto::new)).toList();
        return new PostListResponseDto(postResponseDtoList);

    }

    // 특정 게시글 조회
    public PostResponseDto getPostById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("게시글이 존재하지 않습니다."));

        return new PostResponseDto(post);
    }

    // 게시글 수정
    @Transactional
    public PostResponseDto updatePost(Long id, PostRequestDto requestDto, HttpServletRequest req) {
        User user = findUser(req);
        Post userPost = postRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("게시글이 존재하지 않습니다."));
        if (user.getId().equals(userPost.getUser().getId())) {
            userPost.update(requestDto, user);
            return new PostResponseDto(userPost);
        } else {
            throw new IllegalArgumentException("회원님의 게시글이 아닙니다.");
        }

    }

    // 게시글 삭제
    @Transactional
    public ResponseEntity<StatusMessageDto> deletePost(Long id, HttpServletRequest req) {
        User user = findUser(req);
        Post userPost = postRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("게시글이 존재하지 않습니다."));
        if (user.getId().equals(userPost.getUser().getId())) {
            postRepository.delete(userPost);
            StatusMessageDto statusMessageDto = new StatusMessageDto("게시글 삭제 성공", HttpStatus.OK.value());
            return new ResponseEntity<>(statusMessageDto, HttpStatus.OK);
        } else {
            throw new IllegalArgumentException("회원님의 게시글이 아닙니다.");
        }

    }

    // 토큰 검사 후 User 반환
    public User findUser(HttpServletRequest req) {
        String token = jwtUtil.getJwtFromHeader(req);

        if (jwtUtil.validateToken(token)) {
            Claims claims = jwtUtil.getUserInfoFromToken(token);
            String username = claims.get("username", String.class);
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
        } else {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }
    }

}
