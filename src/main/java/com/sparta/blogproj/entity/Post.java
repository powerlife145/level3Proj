package com.sparta.blogproj.entity;

import com.sparta.blogproj.dto.PostRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity // JPA가 관리할 수 있는 Entity 클래스 지정
@Getter
@Setter
@Table(name = "post") // 매핑할 테이블의 이름을 지정
@NoArgsConstructor
public class Post extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "content", nullable = false)
    private String content;
    @Column(name = "title", nullable = false)
    private String title;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    public Post(PostRequestDto requestDto, User user) {
        this.content = requestDto.getContent();
        this.title = requestDto.getTitle();
        this.user = user;
    }

    public void update(PostRequestDto requestDto,User user) {
        this.content = requestDto.getContent();
        this.title =requestDto.getTitle();
        this.user = user;
    }
}