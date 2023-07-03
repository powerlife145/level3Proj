package com.sparta.blogproj.dto;

import lombok.Getter;

@Getter
public class StatusMessageDto {
    private String message;
    private int statusCode;

    public StatusMessageDto(String message, int statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }
}
