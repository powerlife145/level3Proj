package com.sparta.blogproj.service;

import com.sparta.blogproj.dto.CommentRequestDto;
import com.sparta.blogproj.dto.CommentResponseDto;
import com.sparta.blogproj.dto.StatusMessageDto;
import com.sparta.blogproj.entity.Comment;
import com.sparta.blogproj.entity.Post;
import com.sparta.blogproj.entity.User;
import com.sparta.blogproj.entity.UserRoleEnum;
import com.sparta.blogproj.jwt.JwtUtil;
import com.sparta.blogproj.repository.CommentRepository;
import com.sparta.blogproj.repository.PostRepository;
import com.sparta.blogproj.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
public class CommentService {

    private final PostRepository postRepository;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public CommentService(PostRepository postRepository, JwtUtil jwtUtil, UserRepository userRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }


    public CommentResponseDto createComment(CommentRequestDto requestDto, HttpServletRequest req) {
        User user = findUser(req);

        Post post = postRepository.findById(requestDto.getPostid()).orElseThrow(() ->
                new IllegalArgumentException("해당하는 Post가 없습니다."));

        Comment comment = new Comment(requestDto, user, post);
        Comment saveComment = commentRepository.save(comment);
        CommentResponseDto commentResponseDto = new CommentResponseDto(saveComment);
        return commentResponseDto;

    }

    @Transactional
    public CommentResponseDto updateComment(Long id, CommentRequestDto requestDto, HttpServletRequest req){
        User user = findUser(req);

        if (!user.getRole().equals(UserRoleEnum.ADMIN)  && !user.getUsername().equals(user)) {
            throw new IllegalArgumentException("관리자 또는 게시글 작성자만 수정할 수 있습니다.");
        }

        Comment userComment = commentRepository.findById(id).orElseThrow(()-> new NoSuchElementException("댓 없음"));

        if(!user.getRole().equals(UserRoleEnum.ADMIN)){
            if (user.getId().equals(userComment.getUser().getId())){
                userComment.update(requestDto, user);
                return  new CommentResponseDto(userComment);
            }else{
                throw new NotAuthorException("작성자만 삭제/수정할 수 있습니다.");
            }
        }else{userComment.update(requestDto, user);
            return  new CommentResponseDto(userComment);
        }
    }


    public ResponseEntity<StatusMessageDto> deleteComment(Long id, HttpServletRequest req) {
        User user = findUser(req);

        Comment userComment = commentRepository.findById(id).orElseThrow(() -> new NoSuchElementException("게시물 없죠?"));

        if (!user.getRole().equals(UserRoleEnum.ADMIN)) {
            if (user.getId().equals(userComment.getUser().getId())) {
                commentRepository.delete(userComment);
                StatusMessageDto statusMessageDto = new StatusMessageDto("게시글 삭제 성공", HttpStatus.OK.value());
                return new ResponseEntity<>(statusMessageDto, HttpStatus.OK);
            } else {
                throw new IllegalArgumentException("회원님 나가시죠~");
            }
        } else {
            commentRepository.delete(userComment);
            StatusMessageDto statusMessageDto = new StatusMessageDto("게시글 삭제 성공", HttpStatus.OK.value());
            return new ResponseEntity<>(statusMessageDto, HttpStatus.OK);


        }
    }


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

}
