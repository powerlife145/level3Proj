package com.sparta.blogproj.service;

import com.sparta.blogproj.dto.StatusMessageDto;
import com.sparta.blogproj.dto.UserInformationDto;
import com.sparta.blogproj.entity.User;
import com.sparta.blogproj.jwt.JwtUtil;
import com.sparta.blogproj.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;


    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // 회원 가입
    public ResponseEntity<StatusMessageDto> createUser(UserInformationDto requestDto) {
        userRepository.findByUsername(requestDto.getUsername()).ifPresent(a -> {
            throw new IllegalArgumentException("이미 존재하는 이름입니다.");
        });
        String username = requestDto.getUsername();
        String password = passwordEncoder.encode(requestDto.getPassword());
        User user = new User(username,password);
        userRepository.save(user);
        StatusMessageDto statusMessageDto = new StatusMessageDto("회원가입 성공", HttpStatus.OK.value());
        return new ResponseEntity<>(statusMessageDto, HttpStatus.OK);
    }

    // 로그인
    public ResponseEntity<StatusMessageDto> login(UserInformationDto requestDto, HttpServletResponse res) {
        User user = userRepository.findByUsername(requestDto.getUsername()).orElseThrow(() ->
                new NoSuchElementException("일치하는 회원이 없습니다."));
        String username = requestDto.getUsername();
        String password = requestDto.getPassword();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String token = jwtUtil.createToken(requestDto.getUsername());
        res.setHeader(JwtUtil.AUTHORIZATION_HEADER, token);
        StatusMessageDto statusMessageDto = new StatusMessageDto("로그인 성공", HttpStatus.OK.value());
        return new ResponseEntity<>(statusMessageDto, HttpStatus.OK);
    }
}
