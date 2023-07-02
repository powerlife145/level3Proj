package com.sparta.blogproj.dto;

import lombok.Getter;

@Getter
public class SuccessDto {
    private String message;

    public SuccessDto(String message) {
        this.message = message;
    }
}

