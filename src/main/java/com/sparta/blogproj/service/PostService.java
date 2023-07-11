package com.sparta.blogproj.service;

import com.sparta.blogproj.dto.*;
import com.sparta.blogproj.entity.Post;
import com.sparta.blogproj.entity.User;
import com.sparta.blogproj.entity.UserRoleEnum;
import com.sparta.blogproj.jwt.JwtUtil;
import com.sparta.blogproj.repository.PostRepository;
import com.sparta.blogproj.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.NoSuchElementException;

import static com.sparta.blogproj.entity.UserRoleEnum.ADMIN;


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

        if (!user.getRole().equals(UserRoleEnum.ADMIN)  && !user.getUsername().equals(user)) {
            throw new IllegalArgumentException("관리자 또는 게시글 작성자만 수정할 수 있습니다.");
        }

        Post userPost = postRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("게시글이 존재하지 않습니다."));

//        user.getId().equals(userPost.getUser().getId())
        if(!user.getRole().equals(UserRoleEnum.ADMIN)){
            if (user.getId().equals(userPost.getUser().getId())) {
                userPost.update(requestDto, user);
                return new PostResponseDto(userPost);
            } else {
                throw new NotAuthorException("작성자만 삭제/수정할 수 있습니다.");
            }
        }else{userPost.update(requestDto, user);
            return new PostResponseDto(userPost);}

    }

    // 게시글 삭제
    public ResponseEntity<StatusMessageDto> deletePost(Long id, HttpServletRequest req) {
        User user = findUser(req);

        if (!user.getRole().equals(UserRoleEnum.ADMIN)  && !user.getUsername().equals(user)) {
            throw new IllegalArgumentException("관리자 또는 게시글 작성자만 수정할 수 있습니다.");
        }

        Post userPost = postRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("게시글이 존재하지 않습니다."));

        if(!user.getRole().equals(UserRoleEnum.ADMIN)){
            if (user.getId().equals(userPost.getUser().getId())) {
                postRepository.delete(userPost);
                StatusMessageDto statusMessageDto = new StatusMessageDto("게시글 삭제 성공", HttpStatus.OK.value());
                return new ResponseEntity<>(statusMessageDto, HttpStatus.OK);
            } else {
                StatusMessageDto statusMessageDto = new StatusMessageDto("게시글 삭제 실패", HttpStatus.BAD_REQUEST.value());
                return new ResponseEntity<>(statusMessageDto, HttpStatus.BAD_REQUEST);
            }
        }else{postRepository.delete(userPost);
            StatusMessageDto statusMessageDto = new StatusMessageDto("게시글 삭제 성공", HttpStatus.OK.value());
            return new ResponseEntity<>(statusMessageDto, HttpStatus.OK);
        }
    }


    // 토큰 검사 후 User 반환
    public User findUser(HttpServletRequest req) {
        String token = jwtUtil.getJwtFromHeader(req);

        if (jwtUtil.validateToken(token)) {
            Claims claims = jwtUtil.getUserInfoFromToken(token);
            String username = claims.get("sub", String.class);
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
        } else {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }
    }

    @ControllerAdvice
    public class GlobalExceptionHandler {
        @ExceptionHandler(NotAuthorException.class)

        public ResponseEntity<StatusMessageDto> handleNotAuthor(NotAuthorException e) {

            StatusMessageDto statusMessageDto = new StatusMessageDto(e.getMessage(), HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(statusMessageDto, HttpStatus.BAD_REQUEST);
        }
    }
}
