package com.sparta.blogproj.dto;


import lombok.Getter;

@Getter
public class PostRequestDto {
    private String title;
    private String username;
    private String contents;
    private String password;
}
