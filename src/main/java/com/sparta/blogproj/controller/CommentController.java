package com.sparta.blogproj.controller;

import com.sparta.blogproj.dto.CommentRequestDto;
import com.sparta.blogproj.dto.CommentResponseDto;
import com.sparta.blogproj.dto.StatusMessageDto;
import com.sparta.blogproj.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;
    public CommentController(CommentService commentService){this.commentService=commentService;}

    @PostMapping("/comment")
    public CommentResponseDto createComment(@RequestBody CommentRequestDto requestDto, HttpServletRequest req){
        return commentService.createComment(requestDto,req);
    }

    @PutMapping("/comment/{id}")
    public CommentResponseDto updateComment(@PathVariable Long id, @RequestBody CommentRequestDto requestDto, HttpServletRequest req){
        return commentService.updateComment(id,requestDto,req);
    }

    @DeleteMapping("/comment/{id}")
    public ResponseEntity<StatusMessageDto> deletePost(@PathVariable Long id, HttpServletRequest req){
        return commentService.deleteComment(id, req);
    }



}
