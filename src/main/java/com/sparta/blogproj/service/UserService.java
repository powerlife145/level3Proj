package com.sparta.blogproj.service;

import com.sparta.blogproj.dto.StatusMessageDto;
import com.sparta.blogproj.dto.UserInformationDto;
import com.sparta.blogproj.entity.User;
import com.sparta.blogproj.entity.UserRoleEnum;
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

    // ADMIN_TOKEN
    private final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";

    // 회원 가입
    public ResponseEntity<StatusMessageDto> createUser(UserInformationDto requestDto) {
        userRepository.findByUsername(requestDto.getUsername()).ifPresent(a -> {
            throw new NotAuthorException("중복된 username입니다.");
        });
        String username = requestDto.getUsername();
        String password = passwordEncoder.encode(requestDto.getPassword());

        // 사용자 ROLE 확인
        UserRoleEnum role = UserRoleEnum.USER;

        if (requestDto.isAdmin()) {
            if (!ADMIN_TOKEN.equals(requestDto.getAdminToken())) {
                throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다.");
            }
            role = UserRoleEnum.ADMIN;
        }

        User user = new User(username,password,role);
        userRepository.save(user);
        StatusMessageDto statusMessageDto = new StatusMessageDto("회원가입 성공", HttpStatus.OK.value());
        return new ResponseEntity<>(statusMessageDto, HttpStatus.OK);
    }

    // 로그인
    public ResponseEntity<StatusMessageDto> login(UserInformationDto requestDto, HttpServletResponse res) {
        User user = userRepository.findByUsername(requestDto.getUsername()).orElseThrow(() ->
         new NotAuthorException("일치하는 회원이 없습니다."));

        String password = requestDto.getPassword();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            StatusMessageDto statusMessageDto = new StatusMessageDto("토큰이 유효하지 않습니다.", HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(statusMessageDto, HttpStatus.BAD_REQUEST);

        }

        String token = jwtUtil.createToken(requestDto.getUsername(), requestDto.getRole());
        res.setHeader(JwtUtil.AUTHORIZATION_HEADER, token);
        StatusMessageDto statusMessageDto = new StatusMessageDto("로그인 성공", HttpStatus.OK.value());
        return new ResponseEntity<>(statusMessageDto, HttpStatus.OK);
    }
}
