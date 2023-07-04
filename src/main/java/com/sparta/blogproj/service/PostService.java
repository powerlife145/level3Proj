package com.sparta.blogproj.service;

import com.sparta.blogproj.dto.*;
import com.sparta.blogproj.entity.Post;
import com.sparta.blogproj.entity.User;
import com.sparta.blogproj.jwt.JwtUtil;
import com.sparta.blogproj.repository.PostRepository;
import com.sparta.blogproj.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;


@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public PostService(PostRepository postRepository, UserRepository userRepository, JwtUtil jwtUtil) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    // 회원 가입
    public ResponseEntity<StatusMessageDto> createUser(UserInformationDto requestDto) {
        userRepository.findByUsername(requestDto.getUsername()).ifPresent(a -> {
            throw new IllegalArgumentException("이미 존재하는 이름입니다.");
        });

        User user = new User(requestDto);
        userRepository.save(user);
        StatusMessageDto statusMessageDto = new StatusMessageDto("회원가입 성공", HttpStatus.OK.value());
        return new ResponseEntity<>(statusMessageDto, HttpStatus.OK);
    }


    // 로그인
    public ResponseEntity<StatusMessageDto> login(UserInformationDto requestDto, HttpServletResponse res) {
        User user = userRepository.findByUsername(requestDto.getUsername()).orElseThrow(() ->
                new NoSuchElementException("일치하는 이름이 없습니다."));

        if (!user.getPassword().equals(requestDto.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        String token = jwtUtil.createToken(requestDto.getUsername());
        res.setHeader(JwtUtil.AUTHORIZATION_HEADER,token);
        StatusMessageDto statusMessageDto = new StatusMessageDto("로그인 성공", HttpStatus.OK.value());
        return new ResponseEntity<>(statusMessageDto, HttpStatus.OK);
    }

    // 게시글 생성
    public PostResponseDto createPost(PostRequestDto requestDto, HttpServletRequest req) {
        String token = jwtUtil.getJwtFromHeader(req);

        if (jwtUtil.validateToken(token)) {
            Claims claims = jwtUtil.getUserInfoFromToken(token);
            String username = claims.get("username", String.class);
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

            Post post = new Post(requestDto,user);
            Post savePost = postRepository.save(post);
            PostResponseDto postResponseDto = new PostResponseDto(savePost);
            return postResponseDto;
        } else {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

    }

    // 전체 게시글 조회
    public List<PostResponseDto> getPosts() {
        return postRepository.findAllByOrderByModifiedAtDesc().stream().map(PostResponseDto::new).toList();

    }

    // 특정 게시글 조회
    public PostResponseDto getPostById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("게시글이 존재하지 않습니다."));

        return new PostResponseDto(post);
    }

    // 게시글 수정
//    @Transactional
//    public PostResponseDto updatePost(Long id, PostRequestDto requestDto) {
//        Post post = findPost(id);
//        if (requestDto.getPassword().equals(post.getPassword())) {
//            post.update(requestDto);
//            return new PostResponseDto(post);
//        } else {
//            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
//        }
//
//    }
//
//    // 게시글 삭제
//    @Transactional
//    public SuccessDto deletePost(Long id, PasswordDto password) {
//        Post post = findPost(id);
//
//        if (post.getPassword().equals(password.getPassword())) {
//            postRepository.delete(post);
//            return new SuccessDto("삭제가 완료되었습니다.");
//        } else {
//            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
//        }
//
//    }

    // ID와 일치한 게시글 찾기
    private Post findPost(Long id) {
        return postRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("일치하는 게시글이 없습니다.")
        );
    }

}
