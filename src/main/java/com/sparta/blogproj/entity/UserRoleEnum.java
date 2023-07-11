package com.sparta.blogproj.entity;

public enum UserRoleEnum {      // 사용자의 권한을 정의하는 열거형 클래스
    USER(Authority.USER),  // 사용자 권한. "ROLE_USER" 문자열과 연결됨
    ADMIN(Authority.ADMIN);  // 관리자 권한. "ROLE_ADMIN" 문자열과 연결됨

    private final String authority;  // 열거형 항목의 실제 권한 문자열을 저장할 필드

    UserRoleEnum(String authority) {  // 권한 문자열을 인자로 받는 생성자
        this.authority = authority;  // 인자로 받은 권한 문자열을 필드에 저장
    }

    public String getAuthority() {  // 권한 문자열을 반환하는 getter 메소드
        return this.authority;
    }

    public static class Authority {  // 실제 권한 문자열을 정의한 중첩 클래스
        public static final String USER = "ROLE_USER";  // 사용자 권한 문자열
        public static final String ADMIN = "ROLE_ADMIN";  // 관리자 권한 문자열
    }
}
