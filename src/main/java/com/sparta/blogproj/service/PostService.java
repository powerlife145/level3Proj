package com.sparta.blogproj.service;

import com.sparta.blogproj.dto.PasswordDto;
import com.sparta.blogproj.dto.PostRequestDto;
import com.sparta.blogproj.dto.PostResponseDto;
import com.sparta.blogproj.entity.Post;
import com.sparta.blogproj.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;


@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    // 게시글 생성
    public PostResponseDto createPost(PostRequestDto requestDto) {
        Post post = new Post(requestDto);

        Post savePost = postRepository.save(post);

        PostResponseDto postResponseDto = new PostResponseDto(savePost);

        return postResponseDto;
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
    @Transactional
    public PostResponseDto updatePost(Long id, PostRequestDto requestDto) {
        Post post = findPost(id);
        if (requestDto.getPassword().equals(post.getPassword())) {
            post.update(requestDto);
            return new PostResponseDto(post);
        } else {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

    }

    // 게시글 삭제
    @Transactional
    public String deletePost(Long id, PasswordDto password) {
        Post post = findPost(id);

        if (post.getPassword().equals(password.getPassword())) {
            postRepository.delete(post);
            return "게시글이 삭제되었습니다.";
        } else {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

    }

    // ID와 일치한 게시글 찾기
    public Post findPost(Long id) {
        return postRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("일치하는 게시글이 없습니다.")
        );
    }

}
